# 🎟️ 동시성 제어 프로젝트

## 🚀 콘서트 티케팅 어플리케이션

---

## 🎯 프로젝트 목표

> **"대규모 트래픽 상황에서도 안정적인 티켓팅 처리"**  
> → **동시성 제어**를 핵심으로 하고, **성능 최적화 및 CI/CD 배포 자동화**까지 고려한 실전형 프로젝트입니다.

### ✅ 핵심 포인트

- 🔐 **동시성 제어**
  - 동시에 수많은 사용자가 요청해도 데이터 무결성과 일관성 보장
- ⚡ **성능 최적화**
  - DB **인덱싱(Indexing)** 및 **Redis 기반 캐싱(Caching)** 적용
- 🚀 **CI/CD 및 배포 자동화**
  - Docker, GitHub Actions, AWS를 활용한 **자동 배포 파이프라인 구축**

---
# 👥 팀 소개

## 🦃 팀명: **7면조** (七面鳥)

> 다양한 시각과 기술적 강점을 가진 일곱 명이 각자의 “면”을 살려 협업하는 팀입니다.  
> 하나의 방향만 보지 않고, **전방위적 사고와 전략적 실행**을 추구합니다.  
> 우리는 **다면적 문제를 입체적으로 바라보고, 날카롭게 해결**합니다.

---

### 🧑‍💼 팀 구성

| 역할     | 이름           |
|----------|----------------|
| 👑 팀장   | 김민균          |
| 👨‍💻 팀원  | 김형우, 최희정, 남궁교, 오세훈, 김지은 |

---

### 💡 팀 슬로건

> **"우리는 7개의 면으로, 문제를 입체적으로 본다."**

---

#  🛠 개발 환경 및 기술 스택
<br/>

| 분류         | 기술 스택                                                                                     |
|--------------|----------------------------------------------------------------------------------------------|
| 언어         | ![Java](https://img.shields.io/badge/Java-17-blue?logo=java)                                 |
| 프레임워크   | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)<br>![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-318CE7?logo=hibernate)<br>![Spring AOP](https://img.shields.io/badge/Spring%20AOP-AOP-important) |
| 인증/보안    | ![JWT](https://img.shields.io/badge/JWT-authentication-orange)                                |
| 데이터베이스 | ![MySQL](https://img.shields.io/badge/MySQL-8.x-blue?logo=mysql)                              |
| 캐시 서버    | ![Redis](https://img.shields.io/badge/Redis-Cache-red?logo=redis)                             |
| 빌드/의존성  | ![Gradle](https://img.shields.io/badge/Gradle-Build_Tool-02303A?logo=gradle)<br>![Lombok](https://img.shields.io/badge/Lombok-Annotation-10c2c9?logo=lombok) |
| 테스트 도구  | ![Postman](https://img.shields.io/badge/Postman-API_Testing-orange?logo=postman)              |
| 배포 환경    | ![Docker](https://img.shields.io/badge/Docker-Container-2496ED?logo=docker)<br>![AWS](https://img.shields.io/badge/AWS-Cloud-orange?logo=amazon-aws) |
| IDE          | ![IntelliJ](https://img.shields.io/badge/IntelliJ_IDEA-Ultimate-000000?logo=intellijidea)     |


---


# 📌 Git 브랜치 전략 및 네이밍 규칙

## 🌿 브랜치 종류 및 역할

| 브랜치       | 용도                              | 네이밍 예시               |
|--------------|-----------------------------------|----------------------------|
| `main`       | 실제 배포용, 항상 안정적인 코드         | -                          |
| `develop`    | 개발 통합용, 기능 머지 전 중간 단계     | -                          |
| `feat/*`     | 새로운 기능 개발                     | `feat/post-create`         |
| `fix/*`      | 버그 수정                          | `fix/jwt-token-error`      |
| `refactor/*` | 리팩토링                           | `refactor/user-entity`     |
| `docs/*`     | 문서 작업                           | `docs/readme-update`       |
| `test/*`     | 테스트 코드 작업                     | `test/post-service`        |



## 🔀 브랜치 흐름도
> 📦 **브랜치 흐름 요약**
>
> `feat/*` → `develop` → `main`

## ✅ 브랜치 사용 규칙

- `main` 브랜치에 직접 작업 금지 (⚠️ PR을 통해서만 머지)
- 모든 개발은 기능별 브랜치(`feat/*`)에서 시작
- 기능 완료 시 `develop` 브랜치로 PR 요청
- 기능 통합 및 테스트 완료 후 `main`으로 배포 PR

> ※ 테스트 코드는 별도 브랜치(`test/*`)로 관리하며, 테스트 서버에서만 실행합니다.



## 📋 PR 작성 규칙

- **브랜치명**: `feat/user-login`
- **커밋 메시지**: `feat(auth): 로그인 기능 구현`
- **PR 제목**: `[feat] 로그인 기능 구현`
- **PR 본문**: 작업 내역, 테스트 방법, 관련 이슈 포함

## 🧾 브랜치 예시 리스트
> 📂 **브랜치 예시 목록**
>
> `main`  
> `develop`  
> `feat/signup`  
> `feat/post-feed`  
> `feat/follow-system`  
> `fix/token-refresh`  
> `docs/api-reference`


## 🤝 팀 협업 규칙

- 모든 개발은 **PR 기반 협업**을 원칙으로 합니다.
- 브랜치는 **develop 브랜치에 머지 완료된 이후 삭제**합니다.

<br/>

---
# 📌 API 문서

- [🔗 Notion API 문서 바로가기](https://www.notion.so/teamsparta/API-2292dc3ef51480c59cf8fa23e6c940fd)

---

## 🗂 ERD(Entity Relationship Diagram)

<img width="100%" alt="ERD" src="https://github.com/user-attachments/assets/413b5109-526e-4d4f-bccd-a675e0a00946" />

---

## 🎨 와이어프레임 (Wireframe)

> 서비스 주요 화면 구조입니다.

### 🔐 로그인
<img width="662" alt="로그인 화면" src="https://github.com/user-attachments/assets/a20e1ce3-a502-464c-bfd0-7021ff2ce97d" />

---

### 📝 회원가입
<img width="662" alt="회원가입 화면" src="https://github.com/user-attachments/assets/c21f3753-b845-462a-8b48-4d80c22074bb" />

---

### 👤 사용자 페이지 - 프로필 수정
<img width="662" alt="프로필 수정" src="https://github.com/user-attachments/assets/4b1874e3-04bc-44ce-836e-8f961515df65" />

---

### 🧾 사용자 페이지 - 예매 내역
<img width="662" alt="예매내역" src="https://github.com/user-attachments/assets/d17d0374-ba44-4107-9be9-7c203e249acb" />

---

### 🎤 콘서트 메인 페이지
<img width="424" alt="콘서트 메인" src="https://github.com/user-attachments/assets/8818b4c9-d1b4-449f-90d0-c7dcfb1af216" />

---

### 📄 콘서트 상세 페이지
<img width="424" alt="콘서트 상세" src="https://github.com/user-attachments/assets/bad31116-b53a-4844-acdc-2df5d2030b47" />

---

### 🎫 콘서트 예매 화면
<img width="424" alt="콘서트 예매" src="https://github.com/user-attachments/assets/0d14f6f0-5498-4304-b135-d8dcf0300037" />

---

### 🛠 관리자 페이지 - 일정 등록
<img width="424" alt="일정 등록" src="https://github.com/user-attachments/assets/ef68bc03-c505-43a5-8608-7e7b9180ef68" />

---

### 🛠 관리자 페이지 - 일정 수정
<img width="424" alt="일정 수정" src="https://github.com/user-attachments/assets/93d8fe83-6641-4f07-bc93-546ce779663d" />
