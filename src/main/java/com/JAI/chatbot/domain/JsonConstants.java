package com.JAI.chatbot.domain;

public class JsonConstants {
    public static final String EVENT = """
            아래 포맷으로 JSON만 생성해줘
            시간 정보가 없으면 start_time와 end_time은 null로 설정해줘
            {
            "schedules": [{
            "content": "일정 내용",
            "start_date": "YYYY-MM-DD
            "start_time" : "HH:mm:ss",
            "end_date": "YYYY-MM-DDTHH:mm:ss",
            "end_time" : "HH:mm:ss"
            }]
            }
            """;

    public static final String TODO = """
            아래 포맷으로 JSON만 생성해줘
            시간 정보가 없으면 date의 시간 부분은 null로 설정해줘
            {
            "todos": [{
            "content": "투두 내용",
            "date": "YYYY-MM-DD",
                        
            “time”: “HH:mm:ss”
            }]
            }
            """;

    public static final String INTENTION = """
            이 텍스트가 일정 자동 기입, 투두 자동 기입, 계획 추천, 그외 라는 4가지 카테고리 중에 
            어디에 속하는지 카테고리만 반환해줘
            """;

}
