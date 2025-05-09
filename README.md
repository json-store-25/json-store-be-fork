# Json Store

## 📌 프로젝트 소개
> 이커머스 [Json Store](https://github.com/json-store-25/json-store-be)의 성능을 개선합니다.

----

## 😎 역할분담
|                         팀장                          |                                팀원                                 |                             팀원                             |                               팀원                               |                             팀원                             |
|:---------------------------------------------------:|:-----------------------------------------------------------------:|:----------------------------------------------------------:|:--------------------------------------------------------------:|:----------------------------------------------------------:|
|      [**박성근**](https://github.com/p-seonggeun)      |               [**정신우**](https://github.com/cupokki)               |           [**전준영**](https://github.com/Isonade2)           |              [**김윤영**](https://github.com/yunrry)              |           [**장효리**](https://github.com/hyori526)           |
| <ul><li>AWS 스케일링</li> <li>로드밸런싱</li> <li>CI/CD</li> | <ul><li>재고처리  메시지큐</li> <li>주문 비동기처리</li> <li>상품 검색 redis 캐싱</li> | <ul><li>ElasticSearch</li> <li>DB관련 튜닝</li> | <ul><li>모니터링 시스템</li> <li>비즈니스 메티릭 설계 및 측정</li> <li>DB인덱싱</li> | <ul><li>회원 가입 동시성 문제 해결</li>|

## 🔧 기술 스택
### Back-end

<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=yellow"><img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=yellow"><img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"><img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&logo=elastic&logoColor=white"><img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white"><img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white"><img src="https://img.shields.io/badge/apachekafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white">


### 모니터링/로깅

<img src="https://img.shields.io/badge/prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white"><img src="https://img.shields.io/badge/grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white"><img src="https://img.shields.io/badge/logstash-005571?style=for-the-badge&logo=logstash&logoColor=white"><img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&logo=elastic&logoColor=white"><img src="https://img.shields.io/badge/kibana-005571?style=for-the-badge&logo=kibana&logoColor=white">

### 테스트

<img src="https://img.shields.io/badge/artillery-569A31?style=for-the-badge&&logoColor=white">

### 배포

<img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"><img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"><img src="https://img.shields.io/badge/awselasticloadbalancing-8C4FFF?style=for-the-badge&logo=awselasticloadbalancing&logoColor=white"><img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"><img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">

----

## 💾 산출물
### 팀 노션페이지
[노션페이지](https://www.notion.so/goormkdx/1-1b3c0ff4ce3181d0b27ad3245e50b88d)
### ERD
![image](https://github.com/user-attachments/assets/93fca6d2-2e0d-4f31-8611-295a0021a9ff)
### 아키텍처 구조
![image](https://github.com/user-attachments/assets/5485984b-25cd-478b-b1a4-5e5c3531d6c7)

----

## 🏗️ 실행 방법
- 개발 편의성을 위해 EC2-1(BE), EC2-2(INFRA)는 현재 같은 퍼블릭 서브넷 안에 위치해있습니다.
- 하지만, 보안을 위한다면 EC2-2를 프라이빗 서브넷으로 옮겨야합니다.
- EC2-1의 인스턴스 유형은 t2.micro로도 가능합니다.
- EC2-2의 최초 인스턴스 유형은 t3.3xlarge로 선정했습니다.
- 각각 compose 파일에 필요한 설정파일은 커스텀 하시면 됩니다.


### EC2-1 (BE 서버 인스턴스) 
<details>
    <summary>docker-compose.yml</summary>

```yaml
version: '3.8'

services:
  springboot:
    image: 1999byh/jsonstore:latest
    container_name: springboot
    restart: always
    ports:
      - "8080:8080"
    env_file:
      - .env
    volumes:
      - ./app/firebaseAccessKey.json:/app/firebaseAccessKey.json:ro
    networks:
      - backend-network

  nginx:
    image: nginx:latest
    container_name: nginx
    restart: always
    ports:
      - "80:80"
    volumes:
      - ./nginx/default.conf:/etc/nginx/conf.d/default.conf
    depends_on:
      - springboot
    networks:
      - backend-network

  prometheus:
    image: prom/prometheus:latest
    container_name: json_prometheus
    restart: always
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - prometheus_data:/prometheus
    networks:
      - backend-network

  grafana:
    image: grafana/grafana:latest
    container_name: json_grafana
    restart: always
    ports:
      - "3000:3000"
    volumes:
      - grafana_data:/var/lib/grafana
    depends_on:
      - prometheus
    networks:
      - backend-network

networks:
  backend-network:
    driver: bridge

volumes:
  prometheus_data:
  grafana_data:
```
</details>

<details>
<summary>prometheus.yml</summary>

```yaml
global:
  scrape_interval: 5s

scrape_configs:
  - job_name: 'springboot'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['springboot:8080']
```

</details>

<details>
<summary>default.conf</summary>

```config
server {
    listen 80;
    server_name _;

    location / {
        proxy_pass http://springboot:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```
</details>

### EC2-2 (인프라 인스턴스)
<details>
 <summary>docker-compose.yml</summary>

```yaml
version: "3.8"

services:
  mysql:
    image: mysql:8.0
    container_name: json_mysql
    command:
      - "--innodb-buffer-pool-size=3G"
      - "--innodb-buffer-pool-instances=4"
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: json_store
      MYSQL_USER: json
      MYSQL_PASSWORD: jsonpw
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - json-network

  redis:
    image: redis:7
    container_name: json_redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - json-network

  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.10.0
    container_name: json_elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - xpack.security.http.ssl.enabled=false
      - ES_JAVA_OPTS=-Xms1g -Xmx1g
    ports:
      - "9200:9200"
    mem_limit: 2g
    volumes:
      - es-data:/usr/share/elasticsearch/data
    networks:
      - json-network

  logstash:
    image: docker.elastic.co/logstash/logstash:8.10.0
    container_name: json_logstash
    depends_on:
      - elasticsearch
    volumes:
      - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf:ro
    ports:
      - "5044:5044"
      - "5045:5045"
      - "9600:9600"
    networks:
      - json-network

  kibana:
    image: docker.elastic.co/kibana/kibana:8.10.1
    container_name: json_kibana
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    depends_on:
      - elasticsearch
    ports:
      - "5601:5601"
    networks:
      - json-network

  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_SERVER_ID: 1
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      ZOOKEEPER_INIT_LIMIT: 5
      ZOOKEEPER_SYNC_LIMIT: 2
    ports:
      - "22181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "29092:29092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-ui
    ports:
      - "8088:8080"
    environment:
      - KAFKA_CLUSTERS_0_NAME=local-kafka
      - KAFKA_CLUSTERS_0_BOOTSTRAP_SERVERS=kafka:9092
    depends_on:
      - kafka
  #    restart: unless-stopped

  mysqld-exporter:
    image: quay.io/prometheus/mysqld-exporter
    container_name: mysqld-exporter
    restart: unless-stopped
    command:
      - "--mysqld.username=root:root"
      - "--mysqld.address=mysql:3306"
    networks:
      - json-network

  prometheus:
    image: prom/prometheus
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    networks:
      - json-network

  grafana:
    image: grafana/grafana
    container_name: grafana
    ports:
      - "3000:3000"
    networks:
      - json-network
    depends_on:
      - prometheus

volumes:
  mysql-data:
  redis-data:
  es-data:

networks:
  json-network:
    driver: bridge
```
</details>

<details>
<summary>my.cnf</summary>

```config
# .my.cnf
[client]
user=root
password=root
host=mysql
socket=/var/run/mysqld/mysqld.sock
```

</details>

<details>
<summary>logstash.conf</summary>

```config
input {
  # Spring Boot 로그: TCP 입력 (JSON 포맷)
  tcp {
    port => 5044
    codec => json
  }

  # Nginx 로그: Filebeat를 통한 Beats 입력,
  # 여기서는 json 코덱 옵션을 제거합니다.
  beats {
    port => 5045
  }
}

filter {
  # ★ Nginx 로그 처리 (Filebeat로 전송된 경우, log_type 필드가 추가되어 있음)
  if [log_type] == "nginx_access" {
    json {
      source => "message"
      target => ""
    }
    mutate {
      remove_field => ["host", "agent", "ecs", "log", "input", "@version", "message"]
    }
  }
  # ★ Spring Boot 로그 처리 (메시지에 "처리시간=" 포함)
  else if [message] =~ "처리시간=" {
    grok {
      match => { "message" => "처리시간=%{NUMBER:duration}ms" }
      tag_on_failure => []
    }
    mutate {
      convert => { "duration" => "integer" }
    }
  }
}

output {
  stdout { codec => rubydebug }  # 수신 확인용
  if [log_type] == "nginx_access" {
    elasticsearch {
      hosts => ["http://elasticsearch:9200"]
      index => "nginx_access-%{+YYYY.MM.dd}"
    }
  } else {
    elasticsearch {
      hosts => ["http://elasticsearch:9200"]
      index => "application-logs-%{+YYYY.MM.dd}"
    }
  }
}
```

</details>

<details>

<summary>prometheus.yml</summary>

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'mysqld-exporter'
    static_configs:
      - targets: ['mysqld-exporter:9104']
```

</details>

----
## 📑 성능개선 결과
### 처리량, 응답시간 향상
<h4><b>UUID -> ULID</b></h4>
  
기존에는 PK와 UUID 컬럼을 따로 두고, 비즈니스 로직에서 UUID로 레코드를 조회하는 구조

- UUID: DB(MySQL 등)에서 삽입 시 랜덤 I/O 발생 → 성능 저하 유발

- ULID: 시간 순 정렬 가능 → B-TREE 인덱스 구조에 최적화

조회 키를 ULID형식으로 변경하는 작업 진행

**평균 응답속도 약 5% 개선**

![image](https://github.com/user-attachments/assets/3d018b2b-c47b-4c46-adbb-54295f3f0d2b)
  
<h4><b>캐싱</b></h4>

![image](https://github.com/user-attachments/assets/c19b2a25-65bc-47ca-b0b5-4ebaed346376)
<h4><b>ElasticSearch 엔진 적용</b></h4>

![image](https://github.com/user-attachments/assets/70533c10-925e-4f02-992d-3632e4d3ab22)
![image](https://github.com/user-attachments/assets/1304c286-5bd9-4138-9925-3b84f7134319)
<h4><b>OSIV 설정 OFF</b></h4>

![image](https://github.com/user-attachments/assets/05ef188a-2b64-4855-9b61-ee4ca624bb67)
<h4><b>OSIV, 버퍼 풀 사이즈 조정</b></h4>
- OSIV OFF, 각 응답시간 364ms → 331ms, 약 9% 개선

![image](https://github.com/user-attachments/assets/80eaa0d9-f160-4d1d-ac3f-b7d81af286bc)

- 버퍼 풀 사이즈, 인스턴스 조정 응답시간 409ms -> 348ms, 약 15% 개선

![image](https://github.com/user-attachments/assets/25801cf6-c80d-4207-a50c-c933e476b506)

### 동시성 처리

### 실시간 모니터링 및 성능 대시보드 구성
<h4><b>Spring Boot + Prometheus + Grafana</b></h4>
- 프로메테우스가 일정 시간마다 마이크로미터가 노출한 데이터를 수집해감, 실시간 자원 사용량, 비즈니스 특화 메트릭 측정가능

![image](https://github.com/user-attachments/assets/f41796b5-e8de-4f81-b4ae-294cc042a870)

### 안정적 배포환경 구축

<h4><b>오토스케일링 & 로드밸런싱</b></h4>

- 로드밸런서에 대상 그룹을 설정해 라운드 로빈 방식으로 로드밸런싱
- 80포트로 접속시 443포트로 자동 리다이렉트 되어 HTTPS를 통해서만 접근 가능
- 일정수치 이상의 자원이 지속적으로 사용되는 경우 스케일 아웃
- 사용량이 줄어들면, 자동으로 스케일 인

![image](https://github.com/user-attachments/assets/472ae035-7b1c-49c5-ae35-af9801e42c37)


----

## 🤔 개선방향
### 테스트 코드 미흡
```text
초기 설계 이후에 식별자 ULID가 추가되어 코드 변경점이 너무 많아짐.
이에 따라 추가적인 테스트 코드 마련이 시간적으로 모자랐음
```
### Elastic 기능 활용
```text
ElasticSearch 검색 엔진을 적용하였으나, 
현재는 단순 조회나 키워드 검색에만 적용,
추후 검색방면에서 자동완성, 오타교정 등의 기능이나
판매량, 평점에 가중치를 두어 상위로 노출시키는 전략도 도입할 수 있을듯함.
```
### 무중단 배포 자동화
```text
CI/CD를 통한 무중단 배포를 구현했지만, 
인스턴스가 상시로 켜져있어야하기 때문에 비용 문제로 인한 AMI 교체를 수동으로 트리거해야했음
```
### 재고처리 성능
```text
카프카 쪽. 처리량, 명확한 테스트를  설계, 수행 하지못하였음.  
카프카 적용이 합리적이지 못함. 
어렵고 무거운 카프카 보다 다른 메시지 큐로 해결이 가능 함.  
정확. 데이터 일관성을 위한 추가로직이 필요. 
```
### 부하테스트 목적 모호
```text
단순 API 성능 테스트를 위해 부하테스트 도구를 활용함.  
클라이언트 - 서버 구조에서의
API 성능은 요청 수에 비례해 Linear하게 증가하므로 단건으로 테스트 하여도 되었음.  
부하테스트의 목적은 서비스 임계점 찾기, 스케일 튜닝으로 한정하여도 됨
```
### 성능 개선 방향성
```text
비즈니스 로직, 쿼리 측면에서 접근하기 보단 개선할 수 있는 도구들을 많이 활용하였음.
ex) ElasticSearch, Kafka, DB튜닝 등
전자에서도 성능을 개선시킬 수 있는 여지가 있음.
```
### 운영환경에서의 성능 최적화
```text
로컬과 운영환경의 서버구조와 자원이 다르다보니 같은 개선 방식을 
적용해도 의도했던 결과가 나오지않아 최적화가 어려운 경우가 있었음.
ex)쓰레드 및 커넥션 풀, API 병목 현상
```
### UI 제작
```text
백엔드 관점에서 개발하여 주요한 비즈니스 요구사항 도출이 미흡함. 
사용자가 서비스를 편리하게 이용할 수 있도록 인터페이스 설계와 구현이 필요.
```

---
# ✏️ 느낀점
## 박성근
```text
이번 프로젝트에서는 테스트 방향을 잘못 잡아 초반에 시간을 많이 투자한 것이 가장 아쉬웠습니다.
단순한 클라이언트-서버 구조의 API 성능은 부하가 일정 수준을 넘기지 않으면 급격히 저하되지 않는다는 점을 뒤늦게 깨달아, 처음부터 성능 개선의 초점을 잘못 맞췄습니다. 
본래는 다수의 요청을 통한 부하 상황을 만드는 것보다, 단일 요청의 응답 시간을 측정하고 이를 개선하는 방식이 더 적절했지만, 부하에만 집중한 탓에 비효율적인 방향으로 테스트를 진행하게 되었습니다.
그럼에도 불구하고 이번 경험을 통해 AWS의 ACM, Route 53, VPC, ALB, ASG, AMI, Launch Template 등 다양한 인프라 구성 요소를 직접 다뤄볼 수 있었고, 이를 통해 인프라에 대한 이해도를 높일 수 있었습니다.
다음 프로젝트에서는 초기 설계에 더욱 집중하고, 문서화를 철저히 하여 반복적인 커뮤니케이션 없이도 팀원들과 원활히 협업할 수 있도록 해야겠다고 느꼈습니다.
또한 부하 테스트는 실제 운영 환경에서 필요한 인프라 자원의 한계를 파악하는 용도로 활용하는 것이 효과적이라는 점도 이번 기회를 통해 이해하게 되었습니다.
```

## 정신우
## 전준영
## 김윤영
## 장효리
