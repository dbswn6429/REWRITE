#REWIRTE WEB 입니다.

## 온라인 중고거래 플랫폼
**REWRITE**는 누구나 상품을 등록하고 중고거래를 편하게 할 수 있는 플랫폼 입니다
비대면으로 중고 상품을 거래 할 수 있습니다.

🔗 [REWIRTE GitHub]([https://github.com/dbswn6429/BidCast]) 

---

### 🔧 주요 기능
- **상품등록**:사용자는 중고 상품을 간편하게 등록할 수 있으며, 이미지 ,상품에 대한 영상, 상품 설명, 가격 등을 입력하여 게시할 수 있습니다.
- **관심 상품 등록**:마음에 드는 상품을 관심 목록에 추가하여 마이페이지에서 쉽게 찾아볼 수 있습니다.
- **결제 기능**:원하는 상품을 결제를 거쳐 배송으로 상품을 전달할 수 있습니다.
- **마이페이지**
  - 내가 등록한 상품의 상태(판매 중, 거래 완료 등)를 확인할 수 있습니다.
  - 관심 상품 목록, 완료된 거래 기록 등을 관리할 수 있습니다.
---
### 📦 프로젝트 구성 및 배포
- **Google Cloud Platform (GCP)**를 활용한 서버 배포
- **Docker** 및 **Cloud Run**을 이용해 컨테이너 기반 서비스 운영
- 백엔드: **Java**, **Spring Boot**, **Spring Security**
- 프론트엔드: **HTML5**, **JavaScript**, **CSS3**
- 데이터베이스: **MySQL** 사용, **JPA (Hibernate)**를 통한 ORM 구현
- 인증 및 권한 관리: **Spring Security**와 **JWT를 활용하여 보안 강화**
> ⚠️ **주의**: `application-custom.properties` 파일은 Git에 포함되지 않습니다.
> *직접 `src/main/resources` 경로에 아래 내용을 포함한 파일을 추가해주세요:*

<details>
<summary><code>application-custom.properties</code> 예시 보기</summary>

```
properties
spring.datasource.driver-class-name=net.sf.log4jdbc.sql.jdbcapi.DriverSpy
spring.datasource.url=jdbc:log4jdbc:mysql://34.64.249.125:3306/spring?serverTimezone=Asia/Seoul
spring.datasource.username=<DB아이디>
spring.datasource.password=<DB비밀번호>


# application.properties ??? ??
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

mybatis.configuration.map-underscore-to-camel-case=true
mybatis.type-aliases-package=com.simple.basic.command
# SQL mapper
mybatis.mapper-locations=classpath:/mapper/**/*.xml
```
</details> 

---

### 🛠 사용 기술 스택

> ### 1. Server
> #### 1-1. App Server (유저 서비스)
> - **Build Tools**:`Gradle`
> - **Front-End**: `HTML`, `CSS`, `JavaScript`
> - **Back-End**: `Spring Boot`, `Spring Security`, `JPA`, `Hibernate`
> - **Authorization**: `JWT`, `Spring Security`
>
> #### 1-2. Database
> - `MySQL`

> ### 2. Infrastructure
> - `Google Cloud Platform`, `Docker`, `Cloud Run`

> ### 3. Dev Tools
> - `IntelliJ IDEA`, `Figma`

> ### 4. Collaboration
> - `Git`, `GitHub`
