# 챗봇 승인 멱등성

기준: develop `c07efbdde309232b0dd389b301236ae86c29e9b8`.
첨부 초안의 Ledger/별도 트랜잭션/Lua 구조를 검토해 적용하고 입력 검증, READY 식별 정보,
실제 HTTP 상태, 저장소 장애 응답, 동시성·롤백 통합 테스트를 보완했다.

## 보장 범위와 처리 순서

멱등성은 물리적인 exactly-once 실행이 아니라 `(user_id, approval_token)` 하나에 대해
**커밋된 DB 효과가 최대 한 번** 발생함을 의미한다. 서로 다른 토큰의 같은 제목/날짜는 허용한다.

1. 서버가 기존처럼 UUID 토큰을 생성한다. LLM 결과가 완성되면 READY 스냅샷을 SET NX EX 3600으로
   확정한다. 토큰·종류·항목 순서에서 안정적인 proposalItemId를 만들고 스냅샷 내용의 SHA-256을 저장한다.
2. 승인 API는 DB Ledger를 먼저 조회한다. 완료 결과가 있으면 Redis에 접근하지 않고 원래 ID를 반환한다.
   정규화한 `accept`와 `isAlarmed` 해시가 다르면 409. 거절 시 알람 값은 false로 정규화한다.
3. Lua가 스냅샷 소유자/토큰/READY를 확인하고 `SET NX PX 120000`으로 claimId 임대를 얻는다.
   키는 `approval:{token}:proposal`, `approval:{token}:lease`로 같은 Redis Cluster 슬롯을 쓴다.
4. 별도 Spring Bean의 트랜잭션에서 Ledger INSERT+flush로 UNIQUE를 선점한다. 카테고리 소유권,
   개인 카테고리 여부, 모든 항목의 제목·날짜·시간을 검증하고 Event/Todo/Alarm/Item/결과를 함께 저장한다.
   어느 쓰기든 실패하면 Ledger까지 전부 롤백한다. 알람은 저장된 Event 엔티티에 직접 연결한다.
5. 트랜잭션 프록시가 성공 반환한 뒤에만 Redis를 정리한다. Lua는 claimId가 같을 때만 삭제한다.
   임대가 만료되어 다른 요청이 DB에 진입해도 UNIQUE가 최종 방어선이다. 중복키 예외는 롤백 후
   트랜잭션 바깥에서 처리하고, 해당 Ledger가 있을 때만 재생한다.
6. 커밋 후 정리 실패/응답 유실은 같은 토큰으로 재시도한다. 커밋 결과가 불명확한 저장소 오류도
   503 이후 같은 토큰으로 재시도한다. 미커밋 요청의 임대는 finally 해제하거나 120초 후 만료된다.

## API 호환성

`POST /chatbot/messages`, `POST /chatbot/message?accept=true&isAlarmed=false`, `{"token":"UUID"}`와
기존 `ApiResponse` 외형을 유지한다. 승인 응답의 기존 null data에는 status/replayed/items가 추가된다.
items는 proposalItemId/type/targetId를 담는다. `isAlarmed` 생략 시 false. TODO 알람은 기존처럼 생성하지 않는다.
일정 시작 시간이 없을 때 알람 시각은 기존 규칙(승인 시점의 한국 시간)을 유지한다.

| HTTP | 의미 |
| --- | --- |
| 201 | 최초 승인 또는 거절 커밋 |
| 200 | 동일 결정 재시도, 기존 결과 반환 |
| 202 | 다른 요청 처리 중. 같은 토큰·파라미터로 지수 백오프 재시도 |
| 400 | 잘못된 UUID, 빈 항목, 잘못된 제목/날짜/시간 |
| 403 | 다른 사용자 토큰 또는 본인 개인 카테고리가 아님 |
| 409 | 완료 토큰의 결정/알람 변경, READY/무결성 충돌 |
| 410 | 완료 Ledger도 없고 READY 스냅샷도 없는 만료 토큰 |
| 503 | Redis/DB 접근 또는 트랜잭션 실패. 같은 토큰 재시도 |

## 파일별 변경

경로 기준: `src/main/java/com/JAI/`.

| 파일 | 변경 |
| --- | --- |
| chatbot/approval/domain/ApprovalLedger.java | 사용자+토큰 UNIQUE, 요청/추천안 해시, 결정, 완료 결과 JSON |
| chatbot/approval/domain/ApprovalItem.java | Ledger+추천 항목 UNIQUE, 생성된 대상 ID |
| chatbot/approval/repository/ApprovalLedgerRepository.java | 완료 결과 및 다른 소유자 토큰 조회 |
| chatbot/approval/repository/ApprovalItemRepository.java | 승인 항목 저장 |
| chatbot/approval/dto/ApprovalCommand.java | 트랜잭션 입력 |
| chatbot/approval/dto/ApprovalResult.java | 최초/재시도/처리 중 결과와 안정적인 대상 ID |
| chatbot/approval/dto/ProposalIdentity.java | READY 확정 시 해시 및 결정적 항목 ID |
| chatbot/approval/service/ApprovalPersistenceService.java | 단일 트랜잭션, 전체 입력/권한 검증, DB 알람 저장 |
| chatbot/approval/service/ApprovalOrchestrator.java | DB 우선 재생, 임대, 충돌 처리, 커밋 후 정리 |
| chatbot/approval/redis/ApprovalRedisRepository.java | Lua 선점/소유자 비교 해제/커밋 후 정리 |
| chatbot/RedisChatbotUtil.java | 불변 READY 저장, chatbot: 및 chatbot_token: 삭제 수정 |
| chatbot/controller/dto/ChatbotRedisDataDTO.java | READY 상태·추천 해시·항목 ID 필드 |
| chatbot/controller/ChatbotController.java | 실제 HTTP 상태 및 승인 결과 반환, null alarm 정규화 |
| chatbot/exception/ChatbotExceptionHandler.java | 상태 예외/저장소 오류를 API 외형으로 전달 |
| chatbot/service/ChatbotServiceImpl.java | 기존 승인 진입점 모두 멱등 경로로 위임 |
| event/service/PersonalEventServiceImpl.java | 생성/수정 시 카테고리 소유자 및 그룹 검증 수정 |
| global/util/RedisUtil.java | 원자적 SET NX EX 지원 |

기타 파일:

| 파일 | 변경 |
| --- | --- |
| sql/20261023_chatbot_approval_ledger.sql | MySQL InnoDB Ledger/Item DDL, UNIQUE/FK/토큰 인덱스 |
| build.gradle | Testcontainers MySQL/JUnit 의존성 |
| src/test/java/com/JAI/chatbot/approval/service/ApprovalIntegrationTest.java | 실제 MySQL 8.0.40/Redis 7.2 기반 동시성·롤백·복구·DDL 검증 |
| src/test/java/com/JAI/chatbot/approval/service/ApprovalOrchestratorTest.java | 재생/충돌/장애/임대 해제 단위 테스트 |
| src/test/java/com/JAI/chatbot/approval/repository/ApprovalLedgerRepositoryTest.java | H2 UNIQUE 회귀 테스트 |
| src/test/java/com/JAI/chatbot/controller/ApprovalHttpTest.java | MockMvc 실제 HTTP 상태/응답 code 검증 |
| src/test/java/com/example/JAI/ToDoWithMeApplicationTests.java | 기존 잘못된 애플리케이션 탐색 수정, 외부 비밀 없는 테스트 설정 |
| docs/chatbot-approval-idempotency.md | 설계/변경/테스트/배포/한계 기록 |

## 테스트 실행

Docker Desktop/Engine과 Java 17이 필요하다. 통합 테스트는 Docker가 없으면 조용히 건너뛰지 않고 실패한다.

```sh
./gradlew clean test bootJar
```

실행 결과와 검증 수치는 아래 최종 검증 기록을 참조한다.

## 배포 및 운영

1. DB 백업 및 스테이징 검증 후 `sql/20261023_chatbot_approval_ledger.sql`을 대상 DB에 **한 번** 적용한다.
   Flyway/Liquibase가 없는 저장소이므로 이 스크립트는 자동 실행되지 않는다. 기존 테이블은 변경하지 않는다.
   운영에서는 Hibernate 자동 DDL 변경 대신 관리된 마이그레이션과 validate를 권장한다.
2. 구버전 승인 서버와 신버전을 동시에 서비스하면 구버전이 Ledger를 우회한다. 승인 트래픽을 잠시 중단하고
   모든 승인 처리 인스턴스를 교체한 뒤 재개한다. READY 스냅샷이 없는 구버전 미승인 토큰은 410이므로
   클라이언트가 추천안을 다시 생성하도록 안내한다. 기존 토큰을 호환 목적으로 안전하지 않게 승인하지 않는다.
3. 클라이언트가 실제 201/200/202를 허용하고 202/503을 동일 토큰으로 재시도하도록 확인한다.
   새 토큰을 만들면 별개의 승인 의도가 된다.
4. Ledger는 승인 의도 보존 기간 동안 삭제하지 않는다. 사용자 계정 삭제 시 보존/익명화 정책은 별도로 정해야 한다.
   영속 결과 JSON 구조와 해시 직렬화 규칙을 향후 배포에서 호환되게 유지해야 한다.
5. 503 빈도, Redis 정리 경고, DB UNIQUE/락 대기, 임대 만료를 모니터링한다. Redis TTL은 정리 실패 후 임시 키를 제거한다.
   재시도 가능한 DB 결과를 없애는 Ledger 강제 삭제나 구버전 서버 롤백은 금지한다.

## 남은 위험 / 미검증

- 프로세스 강제 종료, 실제 네트워크 단절, DB failover/커밋 ACK 유실의 운영 수준 chaos 테스트는 수행하지 않았다.
  임대 만료/Redis 우회 DB 경합/커밋 후 정리 예외/응답 재생으로 해당 정합성 경로를 검증한다.
- Redis Cluster 실환경은 미검증이다. Lua의 두 키는 동일 hash tag를 사용한다.
- 외부 알림 전송의 exactly-once는 범위 밖이다. 이번 변경은 Alarm DB 행까지 원자적으로 저장하며,
  기존 스케줄러의 전송/중복 전송 정책은 바꾸지 않는다.
- 과도하게 큰 LLM 응답은 DB TEXT 크기/트랜잭션 시간 한계에 도달할 수 있다. API의 항목 수 제한과
  실제 운영 최대 크기·부하·임대 120초의 적정성은 추가 검증이 필요하다.
- 실제 프런트엔드·인증·LLM을 연결한 종단 간 테스트와 운영 스키마/권한 검증은 배포 전에 수행해야 한다.

## 최종 검증 기록 (2026-10-23 KST)

`./gradlew clean test bootJar`: **BUILD SUCCESSFUL**, 38 tests / 0 failures / 0 errors / 0 skipped.
`git diff --check` 통과. Java 17, Spring Boot 3.3.5, Testcontainers MySQL 8.0.40, Redis 7.2 사용.

| 테스트 클래스 | 통과 |
| --- | ---: |
| ApprovalIntegrationTest | 20 |
| ApprovalOrchestratorTest | 8 |
| ApprovalHttpTest | 8 |
| ApprovalLedgerRepositoryTest | 1 |
| ToDoWithMeApplicationTests | 1 |

- EVENT와 TODO 각각 동시 1/10/50/100건: 각 시나리오 Ledger 1행, 대상 2행, Item 2행.
  EVENT+alarm은 Alarm 2행, TODO는 Alarm 0행. 완료 응답과 재시도 응답의 ID 일치.
- Redis 임대 없이 MySQL에 50건 직접 경쟁: 성공 커밋 1건, Event/Alarm 각각 2행.
- EVENT/TODO/ALARM 두 번째 실제 DB 쓰기 후 예외: Ledger/Item/Event/Todo/Alarm 모두 0행으로 롤백,
  같은 토큰 재시도 성공.
- 승인/거절 50건 경합: Ledger 1행, 최종 결정에 따른 Event 수(승인 2 또는 거절 0).
- 커밋 후 Redis 정리 예외 및 재시도, 스냅샷 삭제 후 기존 ID 재생, 소유자/결정/알람 충돌,
  임대 만료 후 이전 소유자 해제 방지, 만료 토큰, 불변 READY, 잘못된 항목/다른 사용자 카테고리 검증 통과.
- 제공 SQL로 MySQL Ledger/Item 테이블을 다시 만든 후 저장·재생 성공.
- MockMvc HTTP 201/200/202/400/403/409/410/503 및 응답 code 일치 검증 통과.

개발 중 실패도 기록한다: 첫 실행은 기존 기본 테스트의 애플리케이션 탐색 실패(7개 중 1개),
통합 테스트 초기 컴파일은 Container wildcard import 충돌, 이후 실패 주입 3개는 Mockito 인터페이스의
callRealMethod 사용 오류였다. 모두 수정 후 전체 재실행했다. 이전 실행 종료 중 새 clean을 시작해
중간 리포트 파일 경합도 발생했으나 최종 실행은 단독으로 완료했다.
설치된 로컬 MySQL 9.0.1 초기화는 SIGSEGV로 실패해 검증에 사용하지 않았으며,
Docker MySQL 8.0.40으로 대체했다. 과거 대화의 개선율 수치는 주장하지 않는다.
