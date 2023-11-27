# 공부 기록 Socket API

## Socket 연결

local 작업 시 url: `ws://localhost:8080/study/connect`

server 연결 시 url: `wss://domain/study/connect`

- header 필수 포함 내용
    - key: `Authorization`, value: `Bearer {{accessToken}}`

### 공부 시작

- **요청**

```json
{
  "type": "STUDY_START",
  "accessToken": "{{accessToken}}",
  "subjectId": 1
}
```

- **정상 응답**

```json
{
  "result": "OK",
  "message": "Processing success"
}
```

- 오류: 중복 요청

```json
{
  "result": "FAIL",
  "message": "Already processed"
}
```

- 오류: 없는 과목, 혹은 본인의 과목이 아닌 경우

```json
{
  "result": "Not Found",
  "message": "Subject id error"
}
```

- 오류: 유효하지 않은 토큰

```json
{
  "result": "Forbidden",
  "message": "Token error"
}
```

### 공부 종료

- 공부 시간 측정을 종료하려는 경우 Socket 연결을 끊는다.