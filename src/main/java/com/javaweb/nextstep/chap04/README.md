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
- 일단 js, css 파일에 대한 요청도 response로 넘겨줘야 하기 때문에 getRequestHtmlName, convertHtmlToByte 이라는 메서드명을 좀 더 추상적으로 변경하고 <br>
html 파일이 아닌 경우에도 처리가 가능하도록 만들어야한다. 
- client 관련 파일 디렉토리 최상위 폴더인 "webapp"도 변수에 추가하지 말고 클래스 변수로 선언하고 final로 상수화 하는게 나을 것 같다.
- 본격적으로 회원가입에 요청에 대해 생각해보면, 지금까지 했던 페이지를 응답과는 달리 회원가입 이라는 '비즈니스 로직'을 수행해야 한다. 
- 따라서, 비즈니스 로직 수행을 담당하는 '무언가'를 만들어야 할 것 같다. 
- 또한, RequestHandler에서 현재는 페이지 응답에 관련된 것만 수행하는데 다양한 request를 적절하게 처리할 수 있도록 변경해야 할 것 같다. 
- <h4><b> 페이지 요청, 비즈니스 로직 요청 등 각기 다른 요청을 어떻게 구분해야 할까 ? </b></h4>
- <h4><b> 비즈니스 로직 호출하는 요청은 해당 요청을 처리할 로직에 어떻게 맵핑시키면 좋을까 ? </b></h4>

### 배운 것
- Java I/O (InputStream, InputStreamReader, BufferedReader, FileReader 등 - [참고 링크](https://st-lab.tistory.com/41)) 
- 상대경로를 사용할 경우, ./ (현재 위치)는 bin, src 폴더를 포함하는 해당 자바 프로젝트 폴더의 위치이다. 

### 인상 깊었던 말로

