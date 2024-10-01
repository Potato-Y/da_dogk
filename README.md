# 다독 :: 함께 공부하는 독서실
🏫 본 프로젝트는 `전주대학교` `컴퓨터공학과` `모바일 응용` 수업의 팀 프로젝트입니다.

|이름|담당|Github|
|---|---|---|
|유종환|Backend|https://github.com/Potato-Y|
|이동수|Client|https://github.com/dongsu0717|

## 개발 기술 스택
### Backend
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> 개발 당시: `3.1.1` / 현재: `3.3.3` 

<img src="https://img.shields.io/badge/jwt-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white">

### Client
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"> 
<img src="https://img.shields.io/badge/Android-34A853?style=for-the-badge&logo=android&logoColor=white">

## 프로젝트 소개
사용자의 학습 시간을 자동으로 동기화합니다. 동기화된 데이터를 동료들과 공유하는 서비스를 제공합니다.

## 제공 서비스
- 실시간 통신을 통해 공부 시간 동기화
- 공개/비공개 그룹을 통해 그룹원 간의 실시간 공부 시간 공유
- 이메일 인증을 통한 대학교 인증 및 같은 학교 학생들과 공부 시간 공유

## 기대 효과
- 실시간으로 함께 공부하는 동료를 확인할 수 있습니다. 공부 중인 동료를 확인함으로써 뒤처지고 싶지 않은 심리를 유도하고, 이를 통해 높은 의지에 기여합니다.

## 기능 구현
**요구사항이 충족된 기능**
- 회원가입 및 JWT를 통해 사용자 인증
- 표준 WebSocket을 통해 학습 상태를 구분하고, 시간을 측정하여 DB에 기록 (Backend에 한함)
- 사용자별 공부할 과목 관리
- 대학교 이메일을 통한 대학생 인증 (인증 코드 사용)
- 공개/비공개 그룹 시스템 제공, 대학교 전용 그룹 제공

**요구사항이 미충족 된 기능**
- 서버의 비정상 종료 시 공부 종료 시간이 저장되지 않음 (Backend)
- 그룹원의 실시간 공부 정보를 Socket이 아닌 RestAPI로 반복 요청 (Client)

## 프로젝트 시연
`Docker`를 통해 [backend](https://github.com/Potato-Y/da_dogk/tree/main/backend) 프로젝트를 구동하고 사용해 볼 수 있습니다. 

> 본 프로젝트는 민감 정보를 분리하기 위해 `resources` 디렉터리가 `submodule`로 분리되어 있습니다. 정상적으로 의도한 작동을 위해서는 프로젝트에 포함된 Docker Compose를 활용해 주세요.

<br>

1. `Docker`를 준비합니다.
2. 프로젝트를 `clone` 합니다.
    ```bash
    git clone https://github.com/Potato-Y/da_dogk.git
    ```
3. `docker compose`로 앱을 빌드하고 실행합니다.
    ```bash
    docker compose up -d
    ```
4. `80`번 포트를 통해 API를 사용할 수 있습니다. 
    [Postman API Docs](https://documenter.getpostman.com/view/26755334/2s9YXcd5BK)를 참고해 주세요.
    **mail과 관련된 기능은 작동하지 않습니다.**

## 프로젝트 시스템 아키텍처
![팀프 drawio](https://github.com/Potato-Y/da_dogk/assets/68105481/c31d210d-81be-4751-ac29-8b0d6900a074)


## API 문서 안내
개발에 사용된 API 문서는 다음 페이지에서 확인할 수 있습니다.

#### RestAPI
https://github.com/Potato-Y/da_dogk/blob/main/backend/docs/Da%20Dogk%20Api%20Documentation.postman_collection.json

#### Web Socket
https://github.com/Potato-Y/da_dogk/blob/main/backend/docs/StudySocketAPI%20Docs.md

## 프로젝트 이미지
<img width="249" alt="Untitled" src="https://github.com/Potato-Y/da_dogk/assets/68105481/57433359-7302-46b7-ad32-0fc85dcb1dd3">
<img width="249" alt="Untitled (1)" src="https://github.com/Potato-Y/da_dogk/assets/68105481/35bc6d33-d290-4fd5-9b49-74922187bb33">
<img width="247" alt="Untitled (2)" src="https://github.com/Potato-Y/da_dogk/assets/68105481/9c3e6d5b-2127-4112-b887-42e1e4060923">
<img width="242" alt="Untitled (3)" src="https://github.com/Potato-Y/da_dogk/assets/68105481/06932e5c-72ef-4dfc-b9af-fc5bf0413351">
<img width="250" alt="Untitled (4)" src="https://github.com/Potato-Y/da_dogk/assets/68105481/9007539b-b9a6-484e-9111-f5760b67425c">
<img width="244" alt="Untitled (5)" src="https://github.com/Potato-Y/da_dogk/assets/68105481/f559ccd9-53cf-4445-9808-999f5e311df7">
<img width="244" alt="Untitled (1) (1)" src="https://github.com/Potato-Y/da_dogk/assets/68105481/8c5391af-4443-412c-a6d3-2fc222f0639b">
