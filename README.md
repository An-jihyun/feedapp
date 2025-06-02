# Feed Application

소셜 미디어 피드 서비스를 구현한 Spring Boot 기반 RESTful API 프로젝트입니다.

## 📋 목차
- [프로젝트 소개](#-프로젝트-소개)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)
- [API 명세](#-api-명세)
- [설치 및 실행](#-설치-및-실행)
- [아키텍처 결정사항](#-아키텍처-결정사항)

## 🎯 프로젝트 소개

이 프로젝트는 사용자가 게시물을 작성하고, 댓글을 달고, 좋아요를 누르며, 다른 사용자를 팔로우할 수 있는 소셜 미디어 피드 애플리케이션입니다. Spring Security와 JWT를 활용한 인증/인가 시스템을 구현했으며, Soft Delete 방식으로 데이터를 관리합니다.

## ERD
<img width="1045" alt="image" src="https://github.com/user-attachments/assets/e2120fce-7d15-4e80-9314-4102c1e5b21e" />


## ✨ 주요 기능

### 인증 및 사용자 관리
- **회원가입/로그인**: JWT 토큰 기반 인증
- **로그아웃**: 토큰 블랙리스트 관리
- **프로필 관리**: 프로필 조회/수정, 비밀번호 변경
- **회원 탈퇴**: Soft Delete 방식

### 게시물 관리
- **CRUD 기능**: 게시물 작성, 조회, 수정, 삭제
- **권한 검증**: 본인 게시물만 수정/삭제 가능
- **페이지네이션**: 날짜 범위 필터링 지원
- **팔로우 피드**: 팔로우한 사용자들의 게시물 조회

### 댓글 시스템
- **댓글 CRUD**: 게시물에 댓글 작성/수정/삭제
- **권한 관리**: 댓글 작성자 또는 게시물 작성자만 삭제 가능
- **페이지네이션**: 게시물별, 사용자별 댓글 조회

### 좋아요 기능
- **좋아요 토글**: 게시물/댓글에 좋아요
- **제약사항**: 본인 콘텐츠에는 좋아요 불가
- **좋아요 수 조회**: 실시간 카운트

### 팔로우 시스템
- **팔로우/언팔로우**: 다른 사용자 팔로우
- **팔로우 목록**: 팔로잉/팔로워 목록 조회
- **제약사항**: 자기 자신은 팔로우 불가

## 🛠 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.5.0**
- **Spring Security**
- **Spring Data JPA**

### Database
- **MySQL 8.0**

### Security
- **JWT (Json Web Token)**
- **BCrypt Password Encoder**

### Build Tool
- **Gradle 8.13**

### Dependencies
- Lombok
- Validation
- Spring Boot Starter Web
- Spring Boot Starter Security
- JJWT 0.11.5

## 📁 프로젝트 구조

```
src/main/java/com/example/feed/
├── auth/                    # 인증 관련
│   ├── AuthController.java
│   ├── AuthService.java
│   └── dto/
├── comment/                 # 댓글 관련
│   ├── Comment.java
│   ├── CommentController.java
│   ├── CommentService.java
│   ├── CommentRepository.java
│   ├── CommentDomainUtils.java
│   └── dto/
├── common/                  # 공통 컴포넌트
│   ├── BaseEntity.java     # Soft Delete 지원
│   ├── ApiResponse.java
│   └── ErrorResponse.java
├── exception/              # 예외 처리
│   └── GlobalExceptionHandler.java
├── follow/                 # 팔로우 관련
│   ├── Follow.java
│   ├── FollowController.java
│   ├── FollowService.java
│   └── dto/
├── like/                   # 좋아요 관련
│   ├── Like.java
│   ├── LikeController.java
│   ├── LikeService.java
│   └── dto/
├── post/                   # 게시물 관련
│   ├── Post.java
│   ├── PostController.java
│   ├── PostService.java
│   ├── PostDomainUtils.java
│   └── dto/
├── security/               # 보안 설정
│   ├── config/
│   ├── filter/
│   ├── jwt/
│   └── userDetail/
└── user/                   # 사용자 관련
    ├── User.java
    ├── UserController.java
    ├── UserService.java
    └── dto/
```

## 📡 API 명세

### 인증 API
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/logout` | 로그아웃 |

### 사용자 API
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/{id}` | 프로필 조회 |
| PUT | `/api/users/me` | 프로필 수정 |
| PUT | `/api/users/me/password` | 비밀번호 변경 |
| DELETE | `/api/users/delete` | 회원 탈퇴 |

### 게시물 API
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/posts` | 게시물 작성 |
| GET | `/api/posts/{id}` | 게시물 단건 조회 |
| GET | `/api/posts` | 게시물 목록 조회 (페이징) |
| GET | `/api/posts/follows` | 팔로우한 사용자의 게시물 조회 |
| PATCH | `/api/posts/{id}` | 게시물 수정 |
| DELETE | `/api/posts/{id}` | 게시물 삭제 |

### 댓글 API
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/posts/{postId}/comments` | 댓글 작성 |
| GET | `/api/posts/{postId}/comments` | 게시물의 댓글 조회 |
| GET | `/api/users/me/comments` | 내 댓글 목록 조회 |
| GET | `/api/users/{userId}/comments` | 특정 사용자의 댓글 조회 |
| PATCH | `/api/comments/{commentId}` | 댓글 수정 |
| DELETE | `/api/comments/{commentId}` | 댓글 삭제 |

### 좋아요 API
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/likes/{targetType}/{targetId}` | 좋아요 토글 |
| GET | `/api/likes/{targetType}/{targetId}` | 좋아요 상태 조회 |

### 팔로우 API
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/follows` | 팔로우 하기 |
| GET | `/api/users/me/followings` | 팔로잉 목록 조회 |
| GET | `/api/users/me/following/{followingId}` | 팔로잉 단건 조회 |
| GET | `/api/users/me/followers` | 팔로워 목록 조회 |
| GET | `/api/users/me/followers/{followerId}` | 팔로워 단건 조회 |
| DELETE | `/api/users/follows` | 언팔로우 |

## 🚀 설치 및 실행

### 요구사항
- Java 17 이상
- MySQL 8.0 이상
- Gradle 8.13

### 데이터베이스 설정
```sql
CREATE DATABASE feedapp;
```

### 환경 설정
`src/main/resources/application.properties` 파일 수정:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/feedapp
spring.datasource.username=your_username
spring.datasource.password=your_password
jwt.secret=your_jwt_secret_key
```

### 실행 방법
```bash
# 프로젝트 클론
git clone https://github.com/your-username/feedapp.git
cd feedapp

# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

## 🏗 아키텍처 결정사항

### 1. 권한 제어 방식
- **결정**: 유틸리티 클래스를 활용한 중복 코드 관리
- **이유**: Spring Security의 @PreAuthorize보다 팀의 이해도가 높고, 점진적 개선이 가능

### 2. 데이터 삭제 방식
- **결정**: Soft Delete (논리적 삭제) + 서비스별 책임 분리
- **이유**: 데이터 복구 가능성 확보, 각 서비스가 자신의 도메인만 담당

### 3. Service Layer와 인증 객체 의존성
- **결정**: CustomUserDetails 활용으로 성능 개선
- **이유**: 불필요한 DB 조회 제거, 기존 구조 최소 변경

## 📝 주요 설계 특징

### Soft Delete 구현
- BaseEntity를 통한 일관된 삭제 처리
- deleted 플래그와 deletedAt 타임스탬프 관리
- 연관 데이터의 계층적 삭제 처리

### JWT 토큰 관리
- 토큰 블랙리스트를 통한 로그아웃 구현
- 스케줄러를 통한 만료 토큰 자동 정리
- CustomUserDetails로 사용자 정보 캐싱

### 도메인 유틸리티 클래스
- 각 도메인별 공통 로직 분리 (PostDomainUtils, CommentDomainUtils, FollowsDomainUtils)
- 권한 검증, 엔티티 조회 등 반복 코드 최소화

### 예외 처리
- GlobalExceptionHandler를 통한 일관된 에러 응답
- 도메인별 커스텀 예외 클래스 정의
- 명확한 에러 메시지와 HTTP 상태 코드 반환
