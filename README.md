# mini-mvc-framework
仿SpringMVC写简单的mvc框架

[Toc]

## 1, Springmvc基本原理流程

![image-20201207204748987](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207204748987.png)
**SpringMvc本质上就是对Servlet的封装。**

因为创建一个Maven项目，然后在pom文件中增加一个依赖：

```xml
<dependency>
  <groupId>javax.servlet</groupId>
  <artifactId>servlet-api</artifactId>
  <version>2.5</version>
  <!-- 部署在服务器时，不使用这个servlet-api 而使用tomcat的-->
  <scope>provided</scope>
</dependency>
```

---

2，创建DispatcherServlet，并注册到web.xml中

```java
package com.dxh.edu.mvcframework.servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class DxhDispatcherServlet extends HttpServlet {
    /**
     *  接收处理请求
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
    }
}
```

**web.xml：**

```xml
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd" >
<web-app>
  <display-name>Archetype Created Web Application</display-name>
  <servlet>
    <servlet-name>dxhmvc</servlet-name>
    <servlet-class>com.dxh.edu.mvcframework.servlet.DxhDispatcherServlet</servlet-class>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>springmvc.properties</param-value>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>dxhmvc</servlet-name>
    <url-pattern>/*</url-pattern>
  </servlet-mapping>
</web-app>
```

---

## 2，注解开发

因为要使用到注解，所以首先要自定义几个注解：

这里就不赘述如何自定义注解了，详情请看：[https://www.cnblogs.com/peida/archive/2013/04/24/3036689.html](https://www.cnblogs.com/peida/archive/2013/04/24/3036689.html)

**Controller注解：**

```java
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DxhController {
    String value() default "";
}
```

**Service注解：**

```java
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DxhService {
    String value() default "";
}
```

**RequestMapping注解：**

```java
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DxhRequestMapping {
    String value() default "";
}
```

**Autowired注解：**

```java
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DxhAutowired {
    String value() default "";
}
```

### 编写测试代码：

测试代码我们放在同项目中的**com.dxh.demo**包中：

```java
package com.dxh.demo.controller;
import com.dxh.demo.service.IDemoService;
import com.dxh.edu.mvcframework.annotations.DxhAutowired;
import com.dxh.edu.mvcframework.annotations.DxhController;
import com.dxh.edu.mvcframework.annotations.DxhRequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@DxhController
@DxhRequestMapping("/demo")
public class DemoController {
    @DxhAutowired
    private IDemoService demoService;
    /**
     * URL:/demo/query
     */
    @DxhRequestMapping("/query")
    public String query(HttpServletRequest request, HttpServletResponse response, String name){
        return demoService.get(name);
    }
}
```

```java
package com.dxh.demo.service;
public interface IDemoService {
    String get(String name);
}
```

```java
package com.dxh.demo.service.impl;
import com.dxh.demo.service.IDemoService;
import com.dxh.edu.mvcframework.annotations.DxhService;
@DxhService("demoService")
public class IDemoServiceImpl implements IDemoService {
    @Override
    public String get(String name) {
        System.out.println("Service实现类中的Name："+ name);
        return name;
    }
}
```

### 目录结构：

![image-20201207204807765](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207204807765.png)



---

## 3，编写自定义DispatcherServlet中的初始化流程：

在创建好的**DxhDispatcherServlet**中重写**init（）**方法，并在init方法中做初始化配置：

1. 加载配置文件  springmvc.properties
2. 扫描相关的类——扫描注解
3. 初始化Bean对象（实现IOC容器，基于注解）
4. 实现依赖注入
5. 构造一个handleMapping处理器映射器，将配置好的url和method建立映射关系

```java
@Override
public void init(ServletConfig config) throws ServletException {
    //1. 加载配置文件  springmvc.properties
    String contextConfigLocation = config.getInitParameter("contextConfigLocation");
    doLoadConfig(contextConfigLocation);
    //2. 扫描相关的类——扫描注解
    doScan("");
    //3. 初始化Bean对象（实现IOC容器，基于注解）
    doInstance();
    //4. 实现依赖注入
    doAutoWired();
    //5. 构造一个handleMapping处理器映射器，将配置好的url和method建立映射关系
    initHandleMapping();
    System.out.println("MVC 初始化完成");
    //6. 等待请求进入处理请求
}
```

以及5个空方法，这篇文章自定义MVC框架其实就是需要对这5个步骤的编写。

```java
//TODO 5，构造一个映射器
private void initHandleMapping() {
}
//TODO 4,实现依赖注入
private void doAutoWired() {
}
//TODO 3,IOC容器
private void doInstance() {
}
//TODO 2,扫描类
private void doScan(String scanPackage) {
}
//TODO 1，加载配置文件
private void doLoadConfig(String contextConfigLocation) {
}
```

### 3.1 加载配置文件

1. 首先在resource目录中创建一个配置文件——springmvc.properties
   ![image-20201207204836251](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207204836251.png)
   表示要扫描com.dxh.demo下的所有注解。

2. 然后在**web.xml**中进行配置：
   ![image-20201207204850314](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207204850314.png)


这样，就可以通过config.getInitParameter("contextConfigLocation")获得这个路径。

![image-20201207204903303](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207204903303.png)

3. 在**DxhDispatcherServlet**中定义一个属性，我们把加载后的配置文件中的信息，存储在Properties 中

```java
private Properties properties = new Properties();;
```

```java
//1，加载配置文件
private void doLoadConfig(String contextConfigLocation) {
    //根据指定路径加载成流：
    InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
    try {
        properties.load(resourceAsStream);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

### 3.2 扫描相关的类，扫描注解

1. 上一步骤，我们已经把需要扫描的包存在**Properties**中，所以这里需要取出：

```java
//2. 扫描相关的类——扫描注解
doScan(properties.getProperty("scanPackage"));
```

2. 把扫描到的类型的全类名存在一个List<String>中缓存起来，等待使用，在**DxhDispatcherServlet**中定义一个list：

```java
//缓存扫描到的类的全类名
private List<String> classNames = new ArrayList<>();
```

3. 从配置文件中我们得到了一个需要扫描的包名（com.dxh.demo），我们需要根据**classPath+包名**，来得到它实际上**在磁盘上存的路径**，然后**递归**，直到把所有的该包下（包括子包...）所有的**类文件（.class结尾）**。然后**存在在List<String> classNames中**。

```java
//2,扫描类
//scanPackage :com.dxh.demo    package--->磁盘的文件夹（File）
private void doScan(String scanPackage) {
    //1.获得classPath路径
    String clasPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    //2.拼接,得到scanPackage在磁盘上的路径
    String  scanPackagePath= clasPath + scanPackage.replaceAll("\\.","/");
    File pack = new File(scanPackagePath);
    File[] files = pack.listFiles();
    for (File file : files) {
        if (file.isDirectory()){ //子 package
            //递归
            doScan(scanPackage+"."+file.getName());  //com.dxh.demo.controller
        }else if(file.getName().endsWith(".class")){
            String className = scanPackage + "." + file.getName().replace(".class", "");
            classNames.add(className);
        }
    }
}
```

### 3.3 初始化Bean对象（实现IOC容器，基于注解）

上一步骤我们把扫描到的类的**全类名**放到了，list中，那么本次步骤需要遍历整个list：

1. 遍历List，依次得到所有的全类名
2. 通过反射得到类对象
3. 根据类对象判断有没有注解，并区分**controller**和**service**
   1. **controller**，它的id此处不做过多处理，不取value了，用类的首字母小写作为id，保存到IOC容器中。
   2. **service**，service层往往是有接口的，再以接口名为id再存入一份bean到ioc，便于后期根据接口类型注入
4. 完成

**代码实现：**

```plain
//IOC容器
private Map<String,Object> ioc = new HashMap<>();
```

```java
//3,IOC容器
//基于classNames缓存的类的全限定类名，以及反射技术，完成对象创建和管理
private void doInstance()  {
    if (classNames.size()==0) return;
    try{
        for (int i = 0; i < classNames.size(); i++) {
            String className = classNames.get(i);  //com.dxh.demo.controller.DemoController
            //反射
            Class<?> aClass = Class.forName(className);
            //区分controller ，区分service
            if (aClass.isAnnotationPresent(DxhController.class)){
                //controller的id此处不做过多处理，不取value了，用类的首字母小写作为id，保存到IOC容器中
                String simpleName = aClass.getSimpleName();//DemoController
                String lowerFirstSimpleName = lowerFirst(simpleName); //demoController
                Object bean = aClass.newInstance();
                ioc.put(lowerFirstSimpleName,bean);
            }else if (aClass.isAnnotationPresent(DxhService.class)){
                DxhService annotation = aClass.getAnnotation(DxhService.class);
                //获取注解的值
                String beanName = annotation.value();
                //指定了id就以指定的id为准
                if (!"".equals(beanName.trim())){
                    ioc.put(beanName,aClass.newInstance());
                }else{
                    //没有指定id ，首字母小写
                    String lowerFirstSimpleName = lowerFirst(aClass.getSimpleName());
                    ioc.put(lowerFirstSimpleName,aClass.newInstance());
                }
                //service层往往是有接口的，再以接口名为id再存入一分bean到ioc，便于后期根据接口类型注入
                Class<?>[] interfaces = aClass.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    //以接口的类名作为id放入。
                    ioc.put(anInterface.getName(),aClass.newInstance());
                }
            }else {
                continue;
            }
        }
    }catch (Exception e){
        e.printStackTrace();
    }
}
```

### 3.4 实现依赖注入：

上一步骤把所有需要加载的bean，存入了ioc Map中，此时，我们就需要遍历这个map然后依次得到每个bean对象，然后判断对象中有没有被**@****DxhAutowired**修饰的属性。

1. 遍历ioc这个map，得到每个对象
2. 获取对象的字段（属性）信息
3. 判断字段是否被**@DxhAutowired**修饰
4. 判断**@DxhAutowired有没有设置value值**
   1. 有，直接从ioc容器中取出，然后设置属性。
   2. 无，需要根据当前字段的类型注入（接口注入）

**代码实现：**

```java
//4,实现依赖注入
private void doAutoWired() {
    if (ioc.isEmpty()){return;}
    //1，判断容器中有没有被@DxhAutowried注解的属性，如果有需要维护依赖注入关系
    for (Map.Entry<String,Object> entry: ioc.entrySet()){
        //获取bean对象中的字段信息
        Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (!declaredField.isAnnotationPresent(DxhAutowired.class)){
                continue;
            }
            //有该注解：
            DxhAutowired annotation = declaredField.getAnnotation(DxhAutowired.class);
            String beanName = annotation.value(); //需要注入的bean的Id
            if ("".equals(beanName.trim())){
                //没有配置具体的beanId，需要根据当前字段的类型注入（接口注入）  IDemoService
                beanName = declaredField.getType().getName();
            }
            //开启赋值
            declaredField.setAccessible(true);
            try {
                //字段调用，两个参数：(哪个对象的字段，传入什么)
                declaredField.set(entry.getValue(),ioc.get(beanName));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### 3.5 构造一个handleMapping处理器映射器

**构造一个handleMapping处理器映射器，将配置好的url和method建立映射关系****。**

**手写MVC框架最关键的环节**

假设有一个：
![image-20201207204926314](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207204926314.png)


那么如何通过**/demo/query**定位到 DemoController类中的**query**这个方法 ？

>之前我们所有被@DxhController（自定义Controller注解）的类，都存在了ioc 这个map中。
>我们可以遍历这个map，得到每个bean对象
>然后判断是否被@DxhController所修饰（排除@DxhService所修饰的bean）
>然后判断是否被@DxhRequestMapping所修饰，有的话，就取其value值，作为baseUrl
>然后遍历该bean对象中的所有方法，得到被@DxhRequestMapping修饰的方法。得到其value值，作为methodUrl。
>baseUrl + methodUrl = url
>我们把url和当前method绑定起来，存在map中，也就是建立了url和method建立映射关系。
>**代码实现：**

```plain
//handleMapping ，存储url和method直接的映射关系
private Map<String,Object> handleMapping = new HashMap<>();
```

```plain
//5，构造一个映射器,将url和method进行关联
private void initHandleMapping() {
    if (ioc.isEmpty()){return;}
    for (Map.Entry<String,Object> entry: ioc.entrySet()){
        //获取ioc中当前遍历对象的class类型
        Class<?> aClass = entry.getValue().getClass();
        //排除非controller层的类
        if (!aClass.isAnnotationPresent(DxhController.class)){
            continue;
        }
        String baseUrl = "";
        if (aClass.isAnnotationPresent(DxhRequestMapping.class)){
            //Controller层 类上 注解@DxhRequestMapping中的value值
            baseUrl = aClass.getAnnotation(DxhRequestMapping.class).value();
        }
        //获取方法
        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            //排除没有@DxhRequestMapping注解的方法
            if (!method.isAnnotationPresent(DxhRequestMapping.class)){continue;}
            //Controller层 类中方法上 注解@DxhRequestMapping中的value值
            String methodUrl = method.getAnnotation(DxhRequestMapping.class).value();
            String url = baseUrl+methodUrl;
            //建立url和method之间的映射关系，用map缓存起来
            handleMapping.put(url,method);
        }
    }
}
```

## 4，测试一下：

到目前位置，还没有完全写完，但是不妨碍我们测试一下看看刚才写的那部分内容有没有什么问题：

### 完整的pom文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.dxh.edu</groupId>
  <artifactId>mvc</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>war</packaging>
  <name>mvc Maven Webapp</name>
  <url>http://www.example.com</url>
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
  </properties>
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.12</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>servlet-api</artifactId>
      <version>2.5</version>
      <!-- 部署在服务器时，不使用这个servlet-api 而使用tomcat的-->
      <scope>provided</scope>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugins>
  <!-- 编译插件定义编译细节-->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.2</version>
        <configuration>
          <source>11</source>
          <target>11</target>
          <encoding>utf-8</encoding>
          <!-- 告诉编译器，编译的时候记录下形参的真实名称-->
          <compilerArgs>
            <arg>-parameters</arg>
          </compilerArgs>
        </configuration>
      </plugin>    
      <plugin>
        <groupId>org.apache.tomcat.maven</groupId>
        <artifactId>tomcat7-maven-plugin</artifactId>
        <version>2.2</version>
        <configuration>
          <port>8080</port>
          <path>/</path>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

pom文件中加入了一个tomcat插件，并设置端口为8080，因此我们通过tomcat启动项目：
![image-20201207204941665](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207204941665.png)


启动完成后，打开浏览器url中输入：

[http://localhost:8080/demo/query](http://localhost:8080/demo/query)
![image-20201207204955809](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207204955809.png)
![image-20201207205009250](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207205009250.png)


浏览器中什么都没返回（我们的代码还没真正的完成，尚未编写处理请求步骤），同时控制台中打印了MVC初始化完成，可以认为，目前的代码没有明显的缺陷。 我们继续~~~~~


---


## 5，改造initHandleMapping()

### 5.1 为什么改造？

![image-20201207205026719](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207205026719.png)

**DxhDispatcherServlet**这个类继承了**HttpServlet，**并重写了**doGet**和**doPost**方法，在doGet中调用了doPost方法，当我们使用反射调用方法时(**method.invoke(......)**)发现少了一部分参数：
![image-20201207205042319](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207205042319.png)


**因此我们要改造initHandleMapping()，修改url和method的映射关系（不简简单单的存入map中）。**

### 5.2 新建Handler类

```java
package com.dxh.edu.mvcframework.pojo;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
public class Handler {
    //method.invoke(obj,)需要
    private Object controller;
    private Method method;
    //spring中url支持正则
    private Pattern pattern;
    //参数的顺序，为了进行参数绑定  ，Key 参数名， Value 代表第几个参数
    private Map<String,Integer> paramIndexMapping;
    public Handler(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
        this.paramIndexMapping = new HashMap<>();
    }
    //getset方法这里省略，实际代码中需要...
}
```

在Handler类中编写了4个属性：

* **private Object controller**：method.invoke(obj,)需要
* **private Method method**：与url绑定的方法
* **private Pattern pattern**：可以通过正则匹配，也可以直接些String url。
* **private Map<String,Integer> paramIndexMapping**：参数的顺序，为了进行参数绑定 ，Key 参数名， Value 代表第几个参数

### 5.3 修改initHandleMapping()

首先，就不能直接通过Map<url,Method>的得方式进行关系映射了，使用一个list，泛型是刚才创建的Handler。

```java
    //handleMapping ，存储url和method直接的映射关系
//    private Map<String,Method> handleMapping = new HashMap<>();
    private List<Handler> handlerMapping = new ArrayList<>();
```

**改动前，改动后代码对比：**
![image-20201207205058640](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207205058640.png)


**改动后的initHandleMapping()：**

```java
//5，构造一个映射器,将url和method进行关联
private void initHandleMapping() {
    if (ioc.isEmpty()){return;}
    for (Map.Entry<String,Object> entry: ioc.entrySet()){
        //获取ioc中当前遍历对象的class类型
        Class<?> aClass = entry.getValue().getClass();
        //排除非controller层的类
        if (!aClass.isAnnotationPresent(DxhController.class)){
            continue;
        }
        String baseUrl = "";
        if (aClass.isAnnotationPresent(DxhRequestMapping.class)){
            //Controller层 类上 注解@DxhRequestMapping中的value值
            baseUrl = aClass.getAnnotation(DxhRequestMapping.class).value();
        }
        //获取方法
        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            //排除没有@DxhRequestMapping注解的方法
            if (!method.isAnnotationPresent(DxhRequestMapping.class)){continue;}
            //Controller层 类中方法上 注解@DxhRequestMapping中的value值
            String methodUrl = method.getAnnotation(DxhRequestMapping.class).value();
            String url = baseUrl+methodUrl;
            //把method所有信息以及url封装为Handler
            Handler handler = new Handler(entry.getValue(),method, Pattern.compile(url));
            //处理计算方法的参数位置信息
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                //不做太多的参数类型判断，只做：HttpServletRequest request, HttpServletResponse response和基本类型参数
                if (parameter.getType()==HttpServletRequest.class||parameter.getType()==HttpServletResponse.class){
                    //如果时request和response对象，那么参数名称存 HttpServletRequest 和 HttpServletResponse
                    handler.getParamIndexMapping().put(parameter.getType().getSimpleName(),i);
                }else{
                    handler.getParamIndexMapping().put(parameter.getName(),i);
                }
            }
            handlerMapping.add(handler);
        }
    }
}
```

## 6, 请求处理开发 doPost()：

上一步骤，我们配置了 uri和method的映射关系，并封装到了Handler中存入list，那么接下来，就要通过**HttpServletRequest，**取出**uri**，然后找到具体的Handler：

1. 通过**HttpServletRequest**取出**uri**找到具体的**Handler**
2. 得到将调用方法的参数的数组
3. 根据上述数组长度创建一个新的数组（参数数组，传入反射调用的）
4. 通过**req.getParameterMap()**得到前台传来的参数parameterMap
5. 遍历parameterMap
6. 通过**StringUtils.join**方法把**name=1&name=2**格式的参数变为**name[1,2] （需要**commons-lang依赖**）**
7. 参数匹配并设值

```java
private Handler getHandler(HttpServletRequest req) {
    if (handlerMapping.isEmpty()){return null;}
    String url = req.getRequestURI();
    //遍历 handlerMapping
    for (Handler handler : handlerMapping) {
        Matcher matcher = handler.getPattern().matcher(url);
        if (!matcher.matches()){continue;}
        return handler;
    }
    return null;
}
```

```java
 @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        //根据uri获取到能够处理当前请求的Handler（从handlerMapping中（list））
        Handler handler = getHandler(req);
        if (handler==null){
            resp.getWriter().write("404 not found");
            return;
        }
        //参数绑定
        //该方法所有参数得类型数组
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        //根据上述数组长度创建一个新的数组（参数数组，传入反射调用的）
        Object[] paramValues = new Object[parameterTypes.length];
        //以下就是为了向参数数组中设值，而且还得保证参数得顺序和方法中形参顺序一致。
        Map<String,String[]> parameterMap = req.getParameterMap();
        //遍历request中所有的参数 ，（填充除了request、response之外的参数）
        for (Map.Entry<String,String[]> entry: parameterMap.entrySet()){
            //name=1&name=2 name[1,2]
            String value = StringUtils.join(entry.getValue(), ",");// 如同 1,2
            //如果参数和方法中的参数匹配上了，填充数据
            if (!handler.getParamIndexMapping().containsKey(entry.getKey())){continue;}
            //方法形参确实有该参数，找到它得索引位置，对应得把参数值放入paramValues
            Integer index = handler.getParamIndexMapping().get(entry.getKey());
            //把前台传递过来的参数值，填充到对应得位置去
            paramValues[index] = value;
        }
        Integer requestIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName());
        paramValues[requestIndex] = req;
        Integer responseIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName());
        paramValues[responseIndex] = resp;
        //最终调用handler得method属性
        try {
            Object invoke = handler.getMethod().invoke(handler.getController(), paramValues);
//简单操作，把方法返回的数据以字符串的形式写出
resp.getWriter().write(invoke.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
```

## 7，测试：

打开浏览器，url中输入：[http://localhost:8080/demo/query?name=lisi](http://localhost:8080/demo/query?name=lisi)

返回：
![image-20201207205113533](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207205113533.png)

控制台中打印出：
![image-20201207205128613](https://typora-files.oss-cn-beijing.aliyuncs.com/file/image-20201207205128613.png)


OK完成~
