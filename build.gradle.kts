plugins {
	java
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
}

group = "com.github.moepig"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web:3.1.2")
	implementation("redis.clients:jedis:4.3.2")
	implementation(platform("software.amazon.awssdk:bom:2.20.125"))
	implementation("software.amazon.awssdk:sqs:2.20.125")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose:3.1.2")
	developmentOnly("org.springframework.boot:spring-boot-devtools:3.1.2")
	compileOnly("org.projectlombok:lombok:1.18.28")
	annotationProcessor("org.projectlombok:lombok:1.18.28")
	runtimeOnly("com.mysql:mysql-connector-j:8.0.33")
	runtimeOnly("org.postgresql:postgresql:42.6.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.2")
	testImplementation("org.springframework.boot:spring-boot-docker-compose:3.1.2")
	testCompileOnly("org.projectlombok:lombok:1.18.28")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.28")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
