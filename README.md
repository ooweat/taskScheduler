# taskScheduler
> 운영중인 프로세스들을 `주기적`으로 자동 `모니터링`하기 위해 만든 토이 프로젝트.
>> 상세한 설정으로 실무 적용에 성공!
## *Package
### [common]
````
Alert : 카카오 알림톡/SMTP 발송 클래스
ResponseCode : 응답값에 대한 Enum 클래스
ServiceList : 운영중인 프로세스 관리를 위한 클래스
Util : 자주 쓰이는 Util 클래스
````
### [controller] - 초기 접근 방식 구현
````
BatchController : Spring에서 제공하는 스케쥴러를 따름
````

### [service] - 실제 서비스 구현
````
BatchService : 스케쥴러에 따른 배치 서비스 구현 
````

### [mappers] - DB Interface

````
ManagerMapper 
````

### [model] - 편의성을 위해 필수 Model 구현
````
AlertModel : 알림 발송을 위한 객체 클래스
EnumModel : 공통 응답값을 위한 객체 클래스
````
# How to Build
````
Build : maven package
````

# [배포이력]

|TYPE|   Public IP    |Private IP|      Port      |Service|config|
|--------|:--------------:|:----:|:--------------:|:------|:-----:|
|**개발**| aws-privateKey |127.0.0.1| ${server.port} |taskScheduler |application.properties에 따름|

# [deploy]
```
Path: /app/ooweat/taskScheduler
ShellScript: service.sh
CMD Option: service.sh start, service.sh stop, service.sh restart 
```
