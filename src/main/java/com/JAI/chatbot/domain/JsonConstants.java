package com.JAI.chatbot.domain;

import java.time.LocalDateTime;

public class JsonConstants {

    public static final String DATE_INFO = "오늘은 " + LocalDateTime.now() + "이야";

    public static final String EVENT = """
            아래 포맷으로 'JSON만' 생성해줘
            다른 설명이나 텍스트는 추가하지 마
            일정에 대한 시간 정보가 없으면 start_time와 end_time은 null로 설정해줘
            {
             "events": [{
             "title": "일정 내용",
             "startDate": "YYYY-MM-DD
             "startTime" : "HH:mm:ss",
             "endDate": "YYYY-MM-DDTHH:mm:ss",
             "endTime" : "HH:mm:ss"
             }]
            }
            """;

    public static final String TODO = """ 
            아래 포맷으로 'JSON만' 생성해줘
            다른 설명이나 텍스트는 추가하지 마
            투두에 대한 시간 정보가 없으면 date의 시간 부분은 null로 저장하면 돼
             {
             "todos": [{
             "title": "투두 내용",
             "date": "YYYY-MM-DD",
             “time”: “HH:mm:ss”
             }]
             }
            """;

    public static final String INTENTION = """
            이 텍스트가 일정 자동 기입, 투두 자동 기입, 계획 추천, 그외 라는 4가지 카테고리 중에 
            어디에 속하는지 '카테고리만' 반환해줘
            다른 설명이나 텍스트는 추가하지 마
            """;


}
