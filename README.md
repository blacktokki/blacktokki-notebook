# blacktokki-notebook
![Java](https://img.shields.io/badge/java-17-ED8B00)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F)

blacktokki-notebook은 마크다운 노트와 타임라인 컨텐츠 관리를 위한 Spring Boot 기반 백엔드 서비스입니다.

## 주요 기능
* **Spring Boot 기반 아키텍처**
* **JWT 인증 시스템 내장**
* **RESTful API 구조 제공**
* **JPA 기반 CRUD 서비스 통합**
* **Content 구조 트리형 관리**
* **OpenGraph 미리보기 기능**
* **Flyway 기반 DB 마이그레이션 자동화 지원**

## 연관 프로젝트
* Account server(SpringBoot): [blacktokki-account](https://github.com/blacktokki/blacktokki-account)
* Agent server(FastMCP): [blacktokki-notebook-agent](https://github.com/blacktokki/blacktokki-notebook-agent)
* Frontend(React Native): [blacktokki-client](https://github.com/blacktokki/blacktokki-client/tree/master/apps/notebook)
    * [사용자 가이드](https://github.com/blacktokki/blacktokki-client/blob/master/apps/notebook/public/%EC%82%AC%EC%9A%A9%20%EB%B0%A9%EB%B2%95.md)

## Installation

저장소를 복제하고 데이터베이스 설정 스크립트를 실행합니다.

```bash
git clone https://github.com/blacktokki/blacktokki-notebook.git
cd blacktokki-notebook
bash scripts/setup.sh <database_user> <database_password>
```

## Usage

Gradle 래퍼를 사용하여 애플리케이션을 빌드하고 실행합니다.

```bash
# 개발 모드로 실행
./gradlew bootRun

# 또는 JAR 빌드 후 실행
./gradlew build
java -jar build/libs/blacktokki-notebook-0.0.1-SNAPSHOT.jar
```

## Contributing

본 프로젝트는 개인 프로젝트로 외부 기여(Pull Request)나 기능 제안을 받지 않습니다. 필요한 경우 자유롭게 Fork하여 사용하시기 바랍니다.

## License

[MIT](https://choosealicense.com/licenses/mit/)
