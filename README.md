﻿# 🌟Adu Calendar🌟
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

### 🎥시연 영상
<div style="display:flex; flex-wrap:wrap; justify-content:center;">
  <img src="ezgif.com-video-to-gif (27).gif" style="border:1px solid black; margin:5px;" width="200" height="150">
  <img src="ezgif.com-video-to-gif (28).gif" style="border:1px solid black; margin:5px;" width="200" height="150">
  <img src="ezgif.com-video-to-gif (29).gif" style="border:1px solid black; margin:5px;" width="200" height="150">
  <img src="ezgif.com-video-to-gif (30).gif" style="border:1px solid black; margin:5px;" width="200" height="150">
  <img src="ezgif.com-video-to-gif (31).gif" style="border:1px solid black; margin:5px;" width="200" height="150">
  <img src="ezgif.com-video-to-gif (32).gif" style="border:1px solid black; margin:5px;" width="200" height="150">
  <img src="ezgif.com-video-to-gif (33).gif" style="border:1px solid black; margin:5px;" width="200" height="150">
  <img src="ezgif.com-video-to-gif (34).gif" style="border:1px solid black; margin:5px;" width="200" height="150">
  <img src="ezgif.com-video-to-gif (35).gif" style="border:1px solid black; margin:5px;" width="200" height="150">
  <img src="ezgif.com-video-to-gif (36).gif" style="border:1px solid black; margin:5px;" width="200" height="150">
</div>

**ㅤ자세한 기능 구현 사항은**
<a href="files/REST%20API.pdf" target="_blank">api 문서</a>

### 🔗ER-Diagram
<img width="691" alt="erdiagram" src="https://github.com/fastcampusmini03/calendarBE/assets/92681117/ab8142b5-37df-4a56-8a84-e9efe38bcdf3">
