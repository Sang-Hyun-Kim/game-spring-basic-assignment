# Playbook: Docker MySQL 기동

> 툴 중립. Claude Code / Antigravity 양쪽이 이 파일을 읽는다.

## 목적
Lv 1의 전제. 런타임 DB는 **MySQL만** 쓴다. H2는 테스트 전용이다.

## 절차

1. 컨테이너가 이미 도는지 먼저 확인한다. 중복 기동으로 포트를 뺏지 않는다.

   ```bash
   docker ps --filter "publish=3306" --format "{{.Names}}\t{{.Image}}\t{{.Status}}"
   ```

2. 없으면 기동한다. **비밀번호는 사용자에게 확인하고, 임의로 정하지 않는다.**

   ```bash
   docker run -d --name crimson-mysql \
     -e MYSQL_ROOT_PASSWORD=<사용자가 정한 값> \
     -e MYSQL_DATABASE=crimson_citadel \
     -p 3306:3306 mysql:8
   ```

3. 기동 완료를 기다린다(초기화에 10~20초).

   ```bash
   docker exec crimson-mysql mysqladmin ping -uroot -p<비밀번호> --silent
   ```

4. `src/main/resources/application.properties` 의 datasource가 위 컨테이너와 맞는지 확인한다.
   - `spring.jpa.hibernate.ddl-auto` 는 **재시작해도 데이터가 유지되는 값**이어야 한다 (ASSIGNMENT.md Lv 1).

## 확인
`./gradlew bootRun` 로그에서 DB 연결 에러가 사라지면 성공. (다른 종류의 에러가 나는 건 정상 — 다음 레벨로 간다.)

## 주의
- 비밀번호가 담긴 `application.properties` 가 커밋되지 않는지 `git status` 로 확인한다.
- 컨테이너를 지우면 데이터가 사라진다. `docker rm` 은 사용자 승인 없이 실행하지 않는다.
