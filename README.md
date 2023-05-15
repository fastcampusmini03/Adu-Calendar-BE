﻿# 🌟Adu Calendar🌟
## 목차
1. [프로젝트 소개](#)
2. [참여](#참여)
3. [기술 스택](#기술스택)
4. [협업 도구](#협업-도구)
5. [데이터 베이스](#데이터베이스)
6. [구현사항](#구현-사항)
7. [ER-Diagram](#ER-Diagram)

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

**ㅤ자세한 기능 구현 사항은** [api문서]()
### 🔗ER-Diagram
<img width="691" alt="erdiagram" src="https://github.com/fastcampusmini03/calendarBE/assets/92681117/ab8142b5-37df-4a56-8a84-e9efe38bcdf3">
