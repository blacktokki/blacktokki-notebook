# blacktokki-notebook
![Java](https://img.shields.io/badge/java-17-ED8B00)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F)

> Java 기반 Spring Boot 애플리케이션으로, 사용자 인증 및 노트 컨텐츠 관리 기능을 포함한 백엔드 서비스입니다.

## 특징

* **Spring Boot 기반 아키텍처**
* **JWT 인증 시스템 내장**
* **RESTful API 구조 제공**
* **JPA 기반 CRUD 서비스 통합**
* **Content 구조 트리형 관리**
* **OpenGraph 미리보기 기능**
* **Flyway 기반 DB 마이그레이션 자동화 지원**

## 연관 프로젝트
* Account server(SpringBoot): [blacktokki-account](https://github.com/blacktokki/blacktokki-account)
* Agent server(FastMCP) [blacktokki-notebook-agent](https://github.com/blacktokki/blacktokki-notebook-agent)
* Frontend(React Native): [blacktokki-client](https://github.com/blacktokki/blacktokki-client/tree/master/apps/notebook)
    * [사용자 가이드](https://github.com/blacktokki/blacktokki-client/blob/master/apps/notebook/web/%EC%82%AC%EC%9A%A9%20%EB%B0%A9%EB%B2%95.md)

## 프로젝트 구조

```
src/
 ├── main/
 │   ├── java/
 │   │   ├── com.blacktokki.notebook/
 │   │   │   ├── account/    # 사용자 관련 기능 (User 엔티티, 인증 등)
 │   │   │   ├── content/    # 컨텐츠 관리 (노트, 북마크 등)
 │   │   │   ├── core/       # 공통 기능 및 설정
 │   │   │   ├── NotebookApplication.java
 │   └── resources/
 │       └── db/migration/   # Flyway 마이그레이션 스크립트
```

## 주요 기능

### 1. 사용자 인증 및 보안

* `JwtAuthenticationFilter`, `JwtTokenProvider`를 통해 JWT 기반 인증 수행
* 사용자 정보는 `User` 엔티티 및 `AuthenticateDto`로 처리
* Spring Security와 통합된 `SecurityConfig` 구성

### 2. RESTful CRUD 구조

* `RestfulController` 기반 API 제공
* `JpaQueryService` 및 `JpaCommandService`를 통해 DTO/Entity 변환 및 QuerySpec 지원
* `bulkDelete`, `bulkUpdateFields`, `getPage` 등의 유틸 메서드 포함

### 3. 컨텐츠 관리 시스템

* `Content` 엔티티: 계층 구조 기반의 노트, 페이지, 북마크 등 저장
* `ContentService`: soft delete, 순서 정렬, batch 처리 등 고급 기능 제공
* `ContentType`, `ContentOption`: 타입과 옵션 Enum 기반 관리

### 4. 오픈그래프 미리보기 기능

* `OpenGraphService` 통해 URL로부터 `title`, `description`, `image`, `url` 등을 추출
* `PreviewController`를 통해 REST API 방식으로 제공

### 5. Flyway 기반 DB 마이그레이션 자동화

* `SchemaUpdateService`: Hibernate의 Metadata 기반 SQL 생성
* `HibernateInfoHolder`를 통해 Hibernate Metadata 접근
* 자동 마이그레이션 SQL을 `src/main/resources/db/migration`에 저장

## 설치 방법
### account server 설치
    $ git clone https://github.com/blacktokki/blacktokki-account
    $ cd blacktokki-account
    $ bash scripts/setup.sh <github username> <database username> <database password>
### messenger server 설치
    $ cd ../blacktokki-notebook
    $ bash scripts/setup.sh <database username> <database password>

## 실행 방법

```bash
./gradlew bootRun
```

또는

```bash
java -jar build/libs/blacktokki-notebook-<version>.jar
```
