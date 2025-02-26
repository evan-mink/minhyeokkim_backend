# 송금 서비스
RESTful API를 제공하며, Swagger-UI를 통해 API 문서를 확인하고 직접 테스트할 수 있습니다.  

서버 구동 후 http://localhost:8000/swagger-ui/index.html 경로에서 확인 가능합니다.  
**참고:** 위 URL은 로컬 환경에서 기본적으로 설정된 Swagger UI URL입니다.  
배포된 환경에서는 URL이 다를 수 있으니, 실제 배포 환경에 맞게 URL을 수정해주세요.
<br><br>

## Project 구성
- Spring-boot version : 3.4.2
- JDK 17
- MySQL version : 8.4.4
<br><br>

## Docker-Compose 구동
포트 (외부 / 내부)
- db : 33060 / 3306
- app : 8000 / 8000

실행  
`cd /path/to/your/project`  
`cd docker`  
`docker-compse up -d`

종료  
`cd /path/to/your/project`  
`cd docker`  
`docker-compose down`
<br><br>

## Local 구동
profile path: root/src/main/resources/  
active profile: local  
port: 8001

<br><br>

## 디렉토리 구조
```
root
├── docker                          # docker root
│   ├── app
│   │   ├── Dockerfile              # app dockerfile
│   │   └── run.sh                  # app 구동 script
│   ├── db                          
│   │   ├── initdb.d
│   │   │   └── init.sql            # 초기화 sql
│   │   └── my.cnf                  # db 환경설정
│   ├── docker-compose.yml          # docker-compose 구동파일
│   └── remittance.env              # 전역 환경 변수 
│
├── docs
│   ├── erd                         # erd 이미지 파일 
│   └── table-specifications.xlsx   # 테이블 명세서 
│
└── src
    ├── main                        # 소스 코드               
    └── test                        # 테스트 코드
```
<br><br>
## 패키지 구조
```
root/src/main

com.example.remittance 
├── account
│   ├── controller                  # 계좌 관련 API 엔드포인트
│   ├── model                       # 계좌 관련 모델
│   └── service                     # 계좌 관련 서비스
│
├── bank
│   ├── controller                  # 은행 관련 API 엔드포인트
│   ├── model                       # 은행 관련 모델
│   └── service                     # 은행 관련 서비스
│
├── common
│   ├── batch                       # 공통 - batch
│   ├── config                      # 공통 - config
│   ├── converter                   # 공통 - JPA Converter
│   ├── model                       # 공통 - model
│   │   ├── base                    # 공통 - model - base class
│   │   ├── entity                  # 공통 - model - JPA Entity
│   │   └── type                    # 공통 - model - 유형
│   ├── repository                  # 공통 - JPA Repository   
│   ├── service                     # 공통 - 서비스
│   └── util                        # 공통 - 유틸 class
│
└── transaction
    ├── controller                  # 거래 관련 API 엔드포인트
    ├── model                       # 거래 관련 모델
    └── service                     # 거래 관련 서비스
    
  
    
root/src/test

com.example.remittance 
├── integration                     # 통합 테스트 코드
└── unit                            # 단위 테스트 코드
```
<br><br>

## 주요 API
http://localhost:8000/swagger-ui/index.html
### 1. Bank
- **Method**: GET
- **URL**: `/banks`
- **Description**: 모든 은행 목록을 조회합니다. 은행 코드는 계좌 생성에 사용됩니다.
<br><br>
---
### 2. Account  
- **Method**: GET
- **URL**: `/accounts`
- **Description**: 모든 계좌 목록을 조회합니다.
<br><br>
- **Method**: POST
- **URL**: `/accounts`
- **Description**: 계좌를 등록합니다. 등록에 사용하는 은행 코드는 Bank API에서 조회 가능합니다.
<br><br>
---
### 3. Transaction  
- **Method**: GET
- **URL**: `/transactions`
- **Description**: 모든 거래 목록을 조회합니다.
<br><br>
- **Method**: POST
- **URL**: `/transactions/deposit`
- **Description**: 계좌 입금을 처리합니다.
<br><br>
- **Method**: POST
- **URL**: `/transactions/withdraw`
- **Description**: 계좌 출금을 처리합니다.
<br><br>
- **Method**: POST
- **URL**: `/transactions/transfer`
- **Description**: 계좌 이체를 처리합니다.
---