# 🌟Adu Calendar🌟
-------
### Adu Calendar?
>Adu Calendar(아듀 캘린더)는 Annual의 A와 duty의 du의 합성어로, 회사의 연차와 당직을 효율적으로 관리하는 캘린더 서비스를 제공합니다.

### 👩🏻‍💻참여
- 유현주 - 팀장
- 김태헌
- 정재은
- 류동우

### ⚒️기술스택
- **SpringBoot**
- **Rest Api**
- **JPA**
- **QueryDSL**
- **MockMvc**
- **AWS**

### 🔧협업 도구
- **Git**
- **GitHub**
- **Slack**

### 🗂️데이터베이스
- **MySQL**
- **h2**

### 시스템 아키텍쳐 설계
![System Architecture](./files/system_architecture.png)
#### Focus Backend
![BackSystemEnv](./files/back_system_env.png)

### 🗒️구현 사항
#### ㅤ🙋🏻‍♀️**유저 관련 기능**
- 회원가입
- 로그인
- 사용자는 USER(일반 유저)와 ADMIN(관리자)로 구분
- 관리자는 각 유저의 권한 설정 가능
- 개인정보 수정, 삭제
#### ㅤ🗓️️**연차/당직 관련 기능**
- USER는 ADMIN에게 연차/당직 등록, 수정, 삭제 신청
- ADMIN은 USER의 신청을 승인/거절
- 캘린더에서 사용자들의 연차와 당직 보여주기
- USER 본인의 신청 내역과 신청 결과(승인/거절) 보여주기

**ㅤ자세한 기능 구현 사항은**<br>
<a href="files/REST%20API.pdf" target="_blank">api 문서</a>
![REST API 명세서](https://github.com/fastcampusmini03/Adu-Calendar-BE/files/11516244/REST.API.pdf)

### 🔗ER-Diagram
<img width="691" alt="erdiagram" src="https://github.com/fastcampusmini03/calendarBE/assets/92681117/ab8142b5-37df-4a56-8a84-e9efe38bcdf3">

### 🎥시연 영상
#### 로그인
![로그인 시연 영상](https://github.com/fastcampusmini03/Adu-Calendar-BE/assets/87412682/24b37255-c89f-4df6-b1b8-2fe79ca64045)
#### 회원가입
![회원가입 시연 영상](https://github.com/fastcampusmini03/Adu-Calendar-BE/assets/87412682/d11e0e87-5070-4f2e-9602-0d4fb02a7ef8)
#### 전체 일정 조회
![전체 일정 조회](https://github.com/fastcampusmini03/Adu-Calendar-BE/assets/87412682/62bf1427-315a-4a12-9fa4-f2d940478b53)
#### 일정 수정, 삭제 요청 - 권한 X
![일정 수정, 삭제 요청 - 권한 X](https://github.com/fastcampusmini03/Adu-Calendar-BE/assets/87412682/e79d81fd-39e2-4e5f-96df-f78afe8019ce)
#### 로그인 되어 있지 않은 상태에서 수정, 삭제 시도, ADMIN 페이지 조회
![로그인 되어 있지 않은 상태에서 수정, 삭제 시도, 존재하지 않는 페이지 조회](https://github.com/fastcampusmini03/Adu-Calendar-BE/assets/87412682/6577e925-c5de-4dca-a54c-55a7faadf3be)
#### 유저 상태에서 권한이 없는 일정 수정, 삭제 시도, ADMIN 페이지 조회
![유저 상태에서 권한이 없는 일정 수정, 삭제 시도, ADMIN 페이지 조회](https://github.com/fastcampusmini03/Adu-Calendar-BE/assets/87412682/35103778-2efe-4f27-a778-4560be6d90dc)
#### ADMIN 계정의 일정 승인, 수정, 삭제 요청 처리
![ADMIN 계정의 일정 승인, 수정, 삭제 요청 처리](https://github.com/fastcampusmini03/Adu-Calendar-BE/assets/87412682/967ed690-b2e8-4436-aeb6-7c4c0ba6c0c6)
#### 관리자 페이지 - 사용자 관리
![관리자 페이지 - 사용자 관리](https://github.com/fastcampusmini03/Adu-Calendar-BE/assets/87412682/7682ce5d-5212-4e9b-b61b-53e90b56f0f4)
#### 존재하지 않는 페이지 조회
![존재하지 않는 페이지 조회](https://github.com/fastcampusmini03/Adu-Calendar-BE/assets/87412682/813d15a2-6081-4057-8406-f3518cbaa433)
#### 개인 정보 조회, 신청 일정 조회
![개인 정보 조회, 신청 일정 조회](https://github.com/fastcampusmini03/Adu-Calendar-BE/assets/87412682/9507f162-f51d-4865-ad87-89f4a29b1afb)
