# Spring Security LTPA2 - Web Servlet Sample

[![CI build](https://github.com/sephiroth-j/spring-security-ltpa2-sample/actions/workflows/ci-build.yml/badge.svg)](https://github.com/sephiroth-j/spring-security-ltpa2-sample/actions/workflows/ci-build.yml) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Sample project for Web Servlet with Spring Boot to demonstrate the use of [spring-security-ltpa2](https://github.com/sephiroth-j/spring-security-ltpa2-core). It uses [an embedded LDAP](https://docs.spring.io/spring-boot/reference/data/nosql.html#data.nosql.ldap.embedded) as user storage and caches the look-up result with [`@Cacheable`](https://docs.spring.io/spring-boot/reference/io/caching.html).
