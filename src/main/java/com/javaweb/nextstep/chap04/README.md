## Chapter4 : HTTP 웹 서버 구현을 통해 HTTP 이해하기 

### 구현 (의식의 흐름..)
> #### 요구사항1 : http://localhost:8080/index.html로 접속했을 때 webapp 디렉토리의 index.html 파일을 읽어 클라이언트에 응답한다
  - index.html 파일 읽어서 byte 형태로 만들어준 뒤 DataOutputStream을 통해 response 넘기면 되지 않을까.. ?   
  - <h4><b> http://localhost:8080/index.html 라는 요청왔을 때 '/' 이후 부분에 파일명을 어떻게 가져오지 ?? </b></h4>
    - connection.getInputStream() 디버거로 찍어보니 여기엔 없는 것 같다. (connection은 Socket 객체)
    - 값이 없던게 아니라 InputStream은 바이트 단위로 나타내기 때문에 디버거로 찍어도 원하는 값 볼 수 없었던 것. <br>
   (https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html 참고)
    - 따라서, 클라이언트가 요청한 html 파일이 무엇인지 알기 위해 InputStreamReader 활용하여 byte array인 InputStream을 디코딩하고 BufferdReader 이용해서 라인 단위로 읽는다.
  - 최종적으로 아래와 같은 메서드로 구현
```java
 // 클라이언트가 요청한 html 파일 이름 가져온다
    private String getRequestHtmlName(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String requestInfo = bufferedReader.readLine();

        if(!requestInfo.contains(".html")) {
            return null;
        }

        String htmlPage = requestInfo.split(" ")[1];

        return htmlPage.substring(1); // 맨 앞에 '/' 지우기 위함
    }
    // http response를 위해 파일을 byte로 변환(절대경로 ver)
    private byte[] convertHtmlToByte(String fileName) throws IOException {
        String rootPath = System.getProperty("user.dir");
        String filePath = rootPath + "/webapp/" + fileName;
        File file = new File(filePath);

        return Files.readAllBytes(file.toPath());
    }  
    // http response를 위해 파일을 byte로 변환(상대경로 ver - 책 참고)
    private byte[] convertHtmlToByte(String fileName) throws IOException {
        String filePath = "./webapp/" + fileName;
        File file = new File(filePath);

        return Files.readAllBytes(file.toPath());
    }  
```
<br>

> #### 요구사항2 : GET 방식으로 회원가입하기 
- 요구사항1 에서 구현한 메서드를 개선해야 할 것 같다. 
- 일단 js, css 파일에 대한 요청도 response로 넘겨줘야 하기 때문에 getRequestHtmlName, convertHtmlToByte 이라는 메서드명을 좀 더 추상적으로 변경하고 html 파일이 아닌 경우에도 처리가 가능하도록 만들어야한다. 
- client 관련 파일 디렉토리 최상위 폴더인 "webapp"도 변수에 추가하지 말고 클래스 변수로 선언하고 final로 상수화 하는게 나을 것 같다.
- 본격적으로 회원가입에 요청에 대해 생각해보면, 지금까지 했던 페이지를 응답과는 달리 회원가입 이라는 '비즈니스 로직'을 수행해야 한다. 
  - 따라서, 비즈니스 로직 수행을 담당하는 '무언가'를 만들어야 할 것 같다. 
  - 또한, RequestHandler에서 현재는 페이지 응답에 관련된 것만 수행하는데 다양한 request를 적절하게 처리할 수 있도록 변경해야 할 것 같다. 
- <h4><b> 페이지 요청, 비즈니스 로직 요청 등 각기 다른 요청을 어떻게 구분해야 할까 ? </b></h4>
  - 일단 아래처럼 구현해봤는데 이렇게 하드코딩으로 분기시키면 변화에 유연하게 대응하기도 어렵고 생각지 못한 케이스도 있을 것 같다.
  - 어떻게 하는게 좋을지 고민해보고 추후에 책도 참고해보자
  
<details>
   <summary>구분하는 메서드</summary>

```java
 private RequestType getRequestType(HttpRequest httpRequest) {
        String httpMethod = httpRequest.getHttpMethod();
        String requestUrl = httpRequest.getRequestUrl();

        switch (HttpMethod.valueOf(httpMethod)) {
            case GET:
                if(requestUrl.contains("?")) {
                    return RequestType.REQUEST_BUSINESS_LOGIC;
                } else if(requestUrl.equals("/") || requestUrl.contains(".")) {
                    return RequestType.REQUEST_FILE;
                } else {
                    return RequestType.REQUEST_BUSINESS_LOGIC;
                }

            case POST:
                break;
        }

        return null;
    }         
```
</details>
  
- <h4><b> 비즈니스 로직 호출하는 요청은 해당 요청을 처리할 로직에 어떻게 맵핑시키면 좋을까 ? </b></h4>
  - 사용자 요청과 해당 요청을 처리하는데 필요한 메서드를 맵핑시켜주는 역할을 하는 LogicMapper 생성. 
  - RequestHandler 내에 코드를 추가해서 할 수도 있겠지만, 그렇게 되면 책임이 많아지기 때문에 코드가 점점 복잡해질 것이다. <br>
   따라서, RequestHandler의 책임을 '클라이언트의 요청을 받아들이고 어떤 요청인지 판단 하는 것'으로 한정하는게 좋을 것 같다고 생각했다.
  - 일단 아래와 같이 구현했는데, 리플렉션을 마구 썼다. 이 방법 말고는 없을까 ? 
  - 사실, executeMethodWithParamsForGetRequest에서 파라미터 맵핑하는 부분도 파라미터 객체에 기본 생성자가 없으면 paramClass.getDeclaredConstructor().newInstance() 이 부분에서 에러 난다.
  - 그리고 LogicMapper가 최초 한 번만 생성되는 것도 보장되어야 하는 등 아직 미흡한 부분이 많은 것 같다. 

<details>
  <summary>LogicMapper</summary>
  
  ```java
  package webserver;
  
  import logic.UserLogic;
  import model.User;
  import util.HttpRequestUtils;
  
  import java.lang.reflect.InvocationTargetException;
  import java.util.HashMap;
  import java.util.Map;
  import java.util.Optional;
  
  public class LogicMapper {
      static class Execution <T> {
          T targetInstance;
          Class logicClass;
          String methodName;
          Class paramClass;
  
          public Execution(T targetInstance, Class logicClass, String methodName) {
              this.targetInstance = targetInstance;
              this.logicClass = logicClass;
              this.methodName = methodName;
          }
  
          public Execution(T targetInstance, Class logicClass, String methodName, Class paramClass) {
              this.targetInstance = targetInstance;
              this.logicClass = logicClass;
              this.methodName = methodName;
              this.paramClass = paramClass;
          }
  
          public T getTargetInstance() {
              return targetInstance;
          }
  
          public Class getLogicClass() {
              return logicClass;
          }
  
          public String getMethodName() {
              return methodName;
          }
  
          public Class getParamClass() {
              return paramClass;
          }
      }
  
      static Map<String, Execution> getMappingUrl = new HashMap<>();
  
      public LogicMapper() {
          initGetRequest();
      }
  
      private void initGetRequest() {
          getMappingUrl.put("/user/create", new Execution(UserLogic.getInstance(), UserLogic.class, "signup", User.class));
      }
  
      private void initPostRequest() {
  
      }
  
      public byte[] doRequestLogic(HttpRequest httpRequest) throws Exception {
          HttpMethod httpMethod = HttpMethod.valueOf(httpRequest.getHttpMethod());
          String requestUrl = httpRequest.getRequestUrl();
          byte[] response = {};
  
          switch (httpMethod) {
              case GET:
                  response = requestUrl.contains("?") ? executeMethodWithParamsForGetRequest(requestUrl) : executeMethodWithoutParamsForGetRequest(requestUrl);
                  executeMethodWithParamsForGetRequest(requestUrl);
                  break;
          }
  
          return response;
      }
  
      public byte[] executeMethodWithParamsForGetRequest(String requestUrl) throws Exception {
          String[] info = requestUrl.split("\\?");
          String url = info[0];
          Map<String, String> params = HttpRequestUtils.parseQueryString(info[1]);
          Execution execution = Optional.ofNullable(getMappingUrl.get(url)).orElseThrow(NoSuchMethodError::new);
          Class paramClass = execution.getParamClass();
          Object instance = paramClass.getDeclaredConstructor().newInstance();
  
          for(String key : params.keySet()) {
              Optional.ofNullable(paramClass.getDeclaredField(key)).ifPresent((field) -> {
                  field.setAccessible(true);
                  try {
                      field.set(instance, params.get(key));
                  } catch (IllegalAccessException e) {
                      e.printStackTrace();
                  }
              });
          }
  
          execution.getLogicClass().getMethod(execution.getMethodName(), execution.getParamClass()).invoke(execution.getTargetInstance(), instance);
  
          return "SUCCESS".getBytes();
      }
  
      public byte[] executeMethodWithoutParamsForGetRequest(String requestUrl) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
          Execution execution = Optional.ofNullable(getMappingUrl.get(requestUrl)).orElseThrow(NoSuchMethodError::new);
          execution.getLogicClass().getMethod(execution.getMethodName(), execution.getParamClass()).invoke(execution.getTargetInstance());
  
          return "SUCCESS".getBytes();
      }
  }
  ```  
</details>

### 배운 것
- Java I/O (InputStream, InputStreamReader, BufferedReader, FileReader 등 - [참고 링크](https://st-lab.tistory.com/41)) 
- 상대경로를 사용할 경우, ./ (현재 위치)는 bin, src 폴더를 포함하는 해당 자바 프로젝트 폴더의 위치이다. 
- 빌더 패턴

### 인상 깊었던 말

