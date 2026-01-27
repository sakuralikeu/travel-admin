# 技术栈推荐与AI代码生成规范（Java后端版）

## 1. 推荐技术栈（简单且健壮）

### 1.1 前端技术栈

```
核心框架：React 18.x + TypeScript
UI组件库：Ant Design 5.x（成熟稳定，组件丰富）
状态管理：Zustand（轻量级，学习成本低）
路由管理：React Router 6.x
HTTP客户端：Axios
表单处理：React Hook Form + Zod（类型安全）
日期处理：Day.js
工具库：Lodash-es
构建工具：Vite
代码规范：ESLint + Prettier
```

**选择理由：**
- React生态成熟，社区活跃，AI生成代码质量高
- TypeScript提供类型安全，减少运行时错误
- Ant Design开箱即用，组件完善，适合企业应用
- Zustand简单易用，避免Redux的复杂性
- Vite构建速度快，开发体验好

### 1.2 后端技术栈（Java）

```
核心框架：Spring Boot 3.x + Java 17
持久层框架：MyBatis-Plus 3.x（简化CRUD，代码生成强大）
数据库：MySQL 8.0+ / PostgreSQL 15+
数据库连接池：HikariCP（Spring Boot默认）
参数验证：Spring Validation + Hibernate Validator
API文档：Knife4j（Swagger增强版，国产优化）
安全认证：Spring Security + JWT
缓存框架：Spring Cache + Redis（可选）
日志框架：Logback（Spring Boot默认）
工具库：Hutool（国产工具库，功能全面）
构建工具：Maven 3.x
模板引擎：（可选）如需导出用POI
```

**选择理由：**
- Spring Boot开箱即用，约定优于配置，快速开发
- MyBatis-Plus大幅简化CRUD，代码生成器强大
- Java类型安全，IDE支持好，重构友好
- 企业级应用首选，生态成熟，文档完善
- AI对Spring Boot生成代码准确度高

### 1.3 部署与运维

```
容器化：Docker + Docker Compose
应用服务器：内置Tomcat（Spring Boot）
反向代理：Nginx
数据库备份：mysqldump / pg_dump
监控告警：Spring Boot Actuator + Prometheus（可选）
日志收集：ELK（可选）
云服务商：阿里云/腾讯云
```

**选择理由：**
- Docker简化部署，环境一致性
- Spring Boot内置Tomcat，无需额外配置
- Actuator提供健康检查和监控端点
- 成熟的运维工具链

### 1.4 完整技术栈架构图

```
┌─────────────────────────────────────────┐
│           前端层 (React + TS)            │
│  Ant Design + Zustand + React Router    │
└──────────────┬──────────────────────────┘
               │ HTTPS/REST API
┌──────────────▼──────────────────────────┐
│          Nginx (反向代理)                │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│   后端层 (Spring Boot + MyBatis-Plus)    │
│  Spring Security + Validation + Redis    │
└──────────────┬──────────────────────────┘
               │ MyBatis-Plus
┌──────────────▼──────────────────────────┐
│      数据层 (MySQL/PostgreSQL)           │
│       定期备份 + 主从复制(可选)           │
└──────────────────────────────────────────┘
```

---

## 2. AI代码生成规范与规则

### 2.1 项目结构规范

#### 前端项目结构（同前）
```
src/
├── assets/              # 静态资源
├── components/          # 公共组件
│   ├── common/         # 通用组件
│   └── business/       # 业务组件
├── pages/              # 页面组件
│   ├── employees/      # 员工管理
│   ├── customers/      # 客户管理
│   └── orders/         # 订单管理
├── layouts/            # 布局组件
├── hooks/              # 自定义Hooks
├── services/           # API服务层
├── stores/             # 状态管理
├── utils/              # 工具函数
├── types/              # TypeScript类型定义
├── constants/          # 常量定义
├── config/             # 配置文件
└── App.tsx
```

#### 后端项目结构（Spring Boot标准结构）
```
src/main/java/com/company/project/
├── controller/         # 控制器层（接收请求）
├── service/           # 服务接口层
│   └── impl/          # 服务实现类
├── mapper/            # MyBatis Mapper接口
├── entity/            # 实体类（数据库映射）
├── dto/               # 数据传输对象
│   ├── request/       # 请求DTO
│   └── response/      # 响应DTO
├── vo/                # 视图对象（View Object）
├── config/            # 配置类
├── common/            # 公共模块
│   ├── constant/      # 常量定义
│   ├── enums/         # 枚举类
│   ├── exception/     # 自定义异常
│   └── result/        # 统一响应结果
├── interceptor/       # 拦截器
├── filter/            # 过滤器
├── aspect/            # 切面（AOP）
└── utils/             # 工具类

src/main/resources/
├── mapper/            # MyBatis XML映射文件
├── application.yml    # 配置文件
└── application-dev.yml # 开发环境配置
```

### 2.2 命名规范

```java
// 包命名：全小写，多级包用.分隔
com.company.employee.controller
com.company.customer.service.impl

// 类命名：PascalCase（大驼峰）
public class CustomerController {}
public class CustomerServiceImpl {}
public class CustomerMapper {}

// 接口命名：PascalCase，Service接口不加I前缀
public interface CustomerService {}  // ✅ 正确
public interface ICustomerService {} // ❌ 错误

// 方法命名：camelCase（小驼峰）
public Customer getCustomerById(Long id) {}
public void createCustomer(CustomerDTO dto) {}

// 变量命名：camelCase
private String customerName;
private List<Customer> customerList;

// 常量命名：UPPER_SNAKE_CASE
public static final String API_BASE_PATH = "/api";
public static final int MAX_RETRY_COUNT = 3;

// DTO命名：XxxDTO / XxxRequest / XxxResponse
public class CreateCustomerRequest {}
public class CustomerResponse {}
public class CustomerDTO {}

// Entity命名：业务名称（对应表名）
@TableName("customer")
public class Customer {}

// Mapper命名：XxxMapper
public interface CustomerMapper extends BaseMapper<Customer> {}
```

### 2.3 代码分层原则（重要）

#### 三层架构模式（强制执行）

```java
// ❌ 错误：在Controller中直接调用Mapper
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerMapper customerMapper;  // ❌ 直接注入Mapper
    
    @PostMapping
    public Result create(@RequestBody Customer customer) {
        customerMapper.insert(customer);  // ❌ Controller直接操作数据库
        return Result.success(customer);
    }
}

// ✅ 正确：严格分层
// 1. Controller层：处理HTTP请求，参数验证，调用Service
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;
    
    @PostMapping
    @Operation(summary = "创建客户")
    public Result<CustomerResponse> create(
            @RequestBody @Valid CreateCustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return Result.success(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "查询客户详情")
    public Result<CustomerResponse> getById(@PathVariable Long id) {
        CustomerResponse response = customerService.getCustomerById(id);
        return Result.success(response);
    }
}

// 2. Service层：业务逻辑处理
public interface CustomerService {
    CustomerResponse createCustomer(CreateCustomerRequest request);
    CustomerResponse getCustomerById(Long id);
}

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    
    private final CustomerMapper customerMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        // 业务逻辑：检查手机号是否重复
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, request.getPhone());
        Customer existCustomer = customerMapper.selectOne(wrapper);
        
        if (existCustomer != null) {
            throw new BusinessException("该手机号已存在");
        }
        
        // 构建实体
        Customer customer = new Customer();
        BeanUtils.copyProperties(request, customer);
        customer.setCreatedAt(LocalDateTime.now());
        
        // 保存到数据库
        customerMapper.insert(customer);
        
        // 记录操作日志
        log.info("创建客户成功，ID: {}", customer.getId());
        
        // 转换为响应对象
        return convertToResponse(customer);
    }
    
    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return convertToResponse(customer);
    }
    
    private CustomerResponse convertToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        BeanUtils.copyProperties(customer, response);
        return response;
    }
}

// 3. Mapper层：数据访问（MyBatis-Plus提供基础CRUD，无需实现）
public interface CustomerMapper extends BaseMapper<Customer> {
    // MyBatis-Plus自动提供：
    // - insert
    // - deleteById
    // - updateById
    // - selectById
    // - selectList
    // 等常用方法
    
    // 仅需定义复杂查询
    List<Customer> selectCustomerWithEmployee(@Param("level") String level);
}
```

### 2.4 实体类与DTO规范

```java
// ✅ 实体类（Entity）- 对应数据库表
@Data
@TableName("customer")
public class Customer {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String phone;
    
    private String email;
    
    @TableField("customer_level")
    private String level;  // VIP, NORMAL
    
    @TableField("assigned_to")
    private Long assignedTo;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic  // 逻辑删除
    private Integer deleted;
}

// ✅ 请求DTO - 接收前端参数
@Data
public class CreateCustomerRequest {
    
    @NotBlank(message = "客户姓名不能为空")
    @Length(min = 2, max = 50, message = "姓名长度为2-50个字符")
    private String name;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotNull(message = "客户等级不能为空")
    private String level;
    
    private Long assignedTo;
}

// ✅ 响应DTO - 返回给前端
@Data
public class CustomerResponse {
    
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String level;
    private Long assignedTo;
    private String assignedEmployeeName;  // 关联查询结果
    private LocalDateTime createdAt;
    
    // 不返回敏感字段如deleted等
}

// ✅ 查询条件DTO
@Data
public class CustomerQueryRequest {
    
    private String keyword;  // 模糊搜索
    private String level;
    private Long assignedTo;
    
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;
    
    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 100, message = "每页数量最大为100")
    private Integer pageSize = 10;
}
```

### 2.5 统一响应结果封装

```java
// ✅ 统一响应结果类
@Data
public class Result<T> implements Serializable {
    
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }
    
    public static <T> Result<T> success(T data) {
        Result<T> result = success();
        result.setData(data);
        return result;
    }
    
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}

// ✅ 分页响应结果
@Data
public class PageResult<T> {
    private List<T> records;
    private Long total;
    private Long pageNum;
    private Long pageSize;
    private Long totalPages;
    
    public static <T> PageResult<T> of(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotalPages(page.getPages());
        return result;
    }
}
```

### 2.6 异常处理规范

```java
// ✅ 自定义业务异常
public class BusinessException extends RuntimeException {
    
    private Integer code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public Integer getCode() {
        return code;
    }
}

// ✅ 全局异常处理器
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数验证失败：{}", message);
        return Result.error(400, message);
    }
    
    /**
     * SQL异常
     */
    @ExceptionHandler(SQLException.class)
    public Result<?> handleSQLException(SQLException e) {
        log.error("数据库异常：", e);
        return Result.error("数据库操作失败");
    }
    
    /**
     * 未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error("系统内部错误");
    }
}

// ✅ 使用示例
@Service
public class CustomerServiceImpl implements CustomerService {
    
    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            // 抛出业务异常，会被全局异常处理器捕获
            throw new BusinessException("客户不存在");
        }
        return convertToResponse(customer);
    }
}
```

### 2.7 MyBatis-Plus配置与使用

```java
// ✅ MyBatis-Plus配置类
@Configuration
public class MyBatisPlusConfig {
    
    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(
            new PaginationInnerInterceptor(DbType.MYSQL)
        );
        return interceptor;
    }
    
    /**
     * 自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createdAt", 
                    LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updatedAt", 
                    LocalDateTime.class, LocalDateTime.now());
            }
            
            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updatedAt", 
                    LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}

// ✅ Service中使用分页查询
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    
    private final CustomerMapper customerMapper;
    
    @Override
    public PageResult<CustomerResponse> getCustomerPage(CustomerQueryRequest request) {
        // 构建分页对象
        Page<Customer> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(request.getKeyword()), 
                    Customer::getName, request.getKeyword())
               .eq(request.getLevel() != null, 
                    Customer::getLevel, request.getLevel())
               .eq(request.getAssignedTo() != null, 
                    Customer::getAssignedTo, request.getAssignedTo())
               .orderByDesc(Customer::getCreatedAt);
        
        // 执行分页查询
        Page<Customer> resultPage = customerMapper.selectPage(page, wrapper);
        
        // 转换为响应对象
        List<CustomerResponse> responseList = resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // 构建分页结果
        Page<CustomerResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(resultPage, responsePage);
        responsePage.setRecords(responseList);
        
        return PageResult.of(responsePage);
    }
}
```

### 2.8 Spring Security + JWT配置

```java
// ✅ JWT工具类
@Component
public class JwtTokenUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * 生成Token
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
    
    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}

// ✅ JWT认证过滤器
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenUtil jwtTokenUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain chain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (token != null && jwtTokenUtil.validateToken(token)) {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            
            // 设置认证信息到SecurityContext
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        chain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

// ✅ Security配置
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/doc.html", "/webjars/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### 2.9 配置文件规范

```yaml
# application.yml
spring:
  application:
    name: employee-customer-system
  
  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/employee_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: ${DB_PASSWORD:root}
    
    # HikariCP连接池配置
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  # Redis配置（可选）
  redis:
    host: ${REDIS_HOST:localhost}
    port: 6379
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 3000

# MyBatis-Plus配置
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.company.project.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# JWT配置
jwt:
  secret: ${JWT_SECRET:mySecretKey123456789}
  expiration: 86400  # 24小时

# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /

# 日志配置
logging:
  level:
    root: INFO
    com.company.project: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

---

## 3. AI代码生成提示词模板

### 3.1 生成完整功能模块

```
请使用以下技术栈生成一个【客户管理】模块的完整代码：

技术栈：
- 后端：Spring Boot 3.x + Java 17 + MyBatis-Plus + MySQL
- 前端：React 18 + TypeScript + Ant Design

要求：
1. 严格遵循三层架构：Controller -> Service -> Mapper
2. 使用MyBatis-Plus的BaseMapper，简化CRUD
3. 使用Spring Validation进行参数验证
4. 统一返回Result<T>响应格式
5. 全局异常处理
6. 实体类使用Lombok注解
7. 包含完整的CRUD操作
8. 分页查询使用MyBatis-Plus的Page
9. 前端需要表格展示、表单新增编辑、删除确认
10. 添加必要的注释和Swagger文档注解

数据库表结构：
CREATE TABLE customer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL COMMENT '客户姓名',
  phone VARCHAR(11) NOT NULL COMMENT '手机号',
  email VARCHAR(100) COMMENT '邮箱',
  level VARCHAR(20) NOT NULL COMMENT '客户等级：VIP/NORMAL',
  assigned_to BIGINT COMMENT '分配员工ID',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
  INDEX idx_phone (phone),
  INDEX idx_level (level),
  INDEX idx_assigned_to (assigned_to)
);

请分别生成：
1. Entity实体类（使用Lombok和MyBatis-Plus注解）
2. Mapper接口（继承BaseMapper）
3. DTO类（CreateCustomerRequest、UpdateCustomerRequest、CustomerResponse、CustomerQueryRequest）
4. Service接口和实现类
5. Controller类（包含Swagger注解）
6. 前端完整代码（Page组件 + Service + Types）
```

### 3.2 生成Service层代码

```
请生成客户管理的Service层代码，要求：

1. 使用Spring Boot + MyBatis-Plus
2. 接口：CustomerService
3. 实现类：CustomerServiceImpl，使用@Service注解
4. 依赖注入使用@RequiredArgsConstructor（Lombok）
5. 实现以下方法：
   - createCustomer(CreateCustomerRequest request): CustomerResponse
   - updateCustomer(Long id, UpdateCustomerRequest request): CustomerResponse
   - deleteCustomer(Long id): void
   - getCustomerById(Long id): CustomerResponse
   - getCustomerPage(CustomerQueryRequest request): PageResult<CustomerResponse>
   - transferCustomer(Long customerId, Long newEmployeeId): void（需要事务）

6. 业务逻辑要求：
   - createCustomer：检查手机号是否重复
   - updateCustomer：检查客户是否存在
   - deleteCustomer：逻辑删除
   - transferCustomer：使用@Transactional，记录转移日志

7. 使用LambdaQueryWrapper构建查询条件
8. Entity到Response的转换使用BeanUtils.copyProperties
9. 添加必要的日志记录（使用@Slf4j）
10. 抛出BusinessException处理业务异常

请生成完整代码并添加注释。
```

### 3.3 生成Controller层代码

```
请生成客户管理的Controller代码，要求：

1. 使用@RestController和@RequestMapping("/api/customers")
2. 依赖注入CustomerService，使用@RequiredArgsConstructor
3. 实现RESTful API：
   - POST /api/customers - 创建客户
   - PUT /api/customers/{id} - 更新客户
   - DELETE /api/customers/{id} - 删除客户
   - GET /api/customers/{id} - 查询客户详情
   - GET /api/customers - 分页查询客户列表
   - PUT /api/customers/{id}/transfer - 转移客户

4. 使用@Valid进行参数验证
5. 统一返回Result<T>格式
6. 添加Swagger注解（@Tag、@Operation、@Parameter）
7. 添加必要的注释

请生成完整代码。
```

### 3.4 生成Mapper XML

```
请生成客户管理的MyBatis XML映射文件，要求：

1. 文件路径：src/main/resources/mapper/CustomerMapper.xml
2. 命名空间：com.company.project.mapper.CustomerMapper
3. 定义以下SQL：
   - selectCustomerWithEmployee：关联查询客户和员工信息
   - selectCustomerStatistics：统计客户数量（按等级分组）

4. 使用resultMap定义结果映射
5. 使用动态SQL（<if>、<where>）
6. 添加必要的注释

请生成完整XML文件。
```

---

## 4. 代码审查检查清单

生成代码后，使用以下检查清单验证：

### 4.1 架构层面
- [ ] 是否严格遵循三层架构（Controller/Service/Mapper）
- [ ] Controller是否只负责接收请求和返回响应
- [ ] Service是否包含业务逻辑
- [ ] Mapper是否只负责数据访问
- [ ] 是否有循环依赖

### 4.2 Spring规范
- [ ] Bean注入是否使用构造器注入（@RequiredArgsConstructor）
- [ ] 是否正确使用@Service、@RestController等注解
- [ ] 事务注解@Transactional是否正确配置rollbackFor
- [ ] 配置类是否使用@Configuration
- [ ] 是否正确使用@Value注入配置

### 4.3 MyBatis-Plus规范
- [ ] Mapper是否继承BaseMapper<T>
- [ ] Entity是否使用@TableName、@TableId等注解
- [ ] 是否使用LambdaQueryWrapper而非QueryWrapper（类型安全）
- [ ] 分页是否使用Page<T>
- [ ] 逻辑删除是否配置@TableLogic

### 4.4 参数验证
- [ ] DTO是否使用@Valid验证注解
- [ ] 是否使用@NotNull、@NotBlank、@Pattern等注解
- [ ] Controller参数是否添加@Valid
- [ ] 是否有全局异常处理MethodArgumentNotValidException

### 4.5 异常处理
- [ ] 是否定义BusinessException
- [ ] 是否有@RestControllerAdvice全局异常处理
- [ ] 异常是否记录日志
- [ ] 是否返回统一的错误格式

### 4.6 响应格式
- [ ] 是否统一使用Result<T>
- [ ] 分页是否使用PageResult<T>
- [ ] 是否包含code、message、data字段
- [ ] 时间戳格式是否统一

### 4.7 安全性
- [ ] 敏感配置是否使用环境变量
- [ ] 密码是否加密存储（BCrypt）
- [ ] JWT密钥是否安全
- [ ] SQL是否防注入（MyBatis-Plus自动防护）
- [ ] 是否有权限控制

### 4.8 性能优化
- [ ] 数据库索引是否合理
- [ ] 是否避免N+1查询
- [ ] 分页查询是否有pageSize上限
- [ ] 是否使用连接池
- [ ] 是否有必要的缓存（Redis）

### 4.9 代码质量
- [ ] 是否使用Lombok减少样板代码
- [ ] 命名是否规范清晰
- [ ] 是否有必要的注释
- [ ] 是否符合阿里巴巴Java开发规范
- [ ] 日志级别是否合理

### 4.10 文档与测试
- [ ] 是否添加Swagger/Knife4j注解
- [ ] API文档是否完整
- [ ] 关键业务逻辑是否有单元测试
- [ ] 是否有集成测试

---

## 5. 常用命令清单

### 5.1 Maven命令

```bash
# 编译
mvn clean compile

# 打包（跳过测试）
mvn clean package -DskipTests

# 运行Spring Boot应用
mvn spring-boot:run

# 运行测试
mvn test

# 生成代码（MyBatis-Plus代码生成器）
mvn mybatis-plus:generate

# 安装到本地仓库
mvn install
```

### 5.2 数据库相关

```bash
# 备份数据库
mysqldump -u root -p employee_system > backup.sql

# 恢复数据库
mysql -u root -p employee_system < backup.sql

# 连接数据库
mysql -u root -p
```

### 5.3 Docker部署

```bash
# 构建镜像
docker build -t employee-system:latest .

# 运行容器
docker run -d -p 8080:8080 \
  -e DB_PASSWORD=your_password \
  -e JWT_SECRET=your_secret \
  --name employee-system \
  employee-system:latest

# 查看日志
docker logs -f employee-system

# 停止容器
docker stop employee-system
```

### 5.4 Dockerfile示例

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 6. MyBatis-Plus代码生成器

```java
/**
 * MyBatis-Plus代码生成器配置
 */
public class CodeGenerator {
    
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/employee_system", "root", "password")
                .globalConfig(builder -> {
                    builder.author("Your Name")
                           .outputDir(System.getProperty("user.dir") + "/src/main/java")
                           .commentDate("yyyy-MM-dd");
                })
                .packageConfig(builder -> {
                    builder.parent("com.company.project")
                           .entity("entity")
                           .mapper("mapper")
                           .service("service")
                           .serviceImpl("service.impl")
                           .controller("controller")
                           .xml("mapper");
                })
                .strategyConfig(builder -> {
                    builder.addInclude("customer", "employee", "order")  // 要生成的表
                           .entityBuilder()
                           .enableLombok()
                           .enableTableFieldAnnotation()
                           .logicDeleteColumnName("deleted")
                           .addTableFills(
                               new Column("created_at", FieldFill.INSERT),
                               new Column("updated_at", FieldFill.INSERT_UPDATE)
                           )
                           .controllerBuilder()
                           .enableRestStyle()
                           .serviceBuilder()
                           .formatServiceFileName("%sService");
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
```

---

## 7. pom.xml依赖配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.company</groupId>
    <artifactId>employee-system</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <knife4j.version>4.4.0</knife4j.version>
        <hutool.version>5.8.24</hutool.version>
        <jwt.version>0.12.3</jwt.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Spring Boot Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- Spring Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        
        <!-- MySQL驱动 -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Knife4j（Swagger增强） -->
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
            <version>${knife4j.version}</version>
        </dependency>
        
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Hutool工具库 -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>${hutool.version}</version>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- Redis（可选） -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        
        <!-- 测试 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 8. 总结

### Java后端版技术栈的优势：

1. **企业级标准**：Spring Boot是Java企业应用的事实标准
2. **类型安全**：强类型语言，编译期检查错误
3. **生态成熟**：丰富的第三方库和工具
4. **性能优越**：JVM优化，适合高并发场景
5. **AI友好**：Spring Boot代码模式固定，AI生成准确度高

### 与Node.js版本的对比：

| 特性 | Java (Spring Boot) | Node.js (Express) |
|------|-------------------|-------------------|
| 类型安全 | ✅ 编译期强类型 | ⚠️ 需要TypeScript |
| 性能 | ✅ 高并发性能好 | ⚠️ 单线程，CPU密集型弱 |
| 企业应用 | ✅ 广泛使用 | ⚠️ 相对较少 |
| 学习曲线 | ⚠️ 较陡峭 | ✅ 较平缓 |
| 开发速度 | ⚠️ 代码量较多 | ✅ 快速开发 |
| 部署复杂度 | ⚠️ 需要JVM | ✅ 部署简单 |

### 推荐使用Java的场景：

- ✅ 团队熟悉Java生态
- ✅ 需要处理高并发业务
- ✅ 对类型安全要求高
- ✅ 企业级长期维护项目
- ✅ 需要强大的ORM和事务支持

通过遵循以上规范，可以确保Java后端代码的**规范性、扩展性和复用性**，非常适合让AI生成高质量的Spring Boot代码！