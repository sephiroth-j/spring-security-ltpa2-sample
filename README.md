# Spring Security LTPA2 - Sample

[![Build Status](https://travis-ci.com/sephiroth-j/spring-security-ltpa2-sample.svg?branch=master)](https://travis-ci.com/sephiroth-j/spring-security-ltpa2-sample) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Sample project with Spring Boot 2.1 to demonstrate the use of [spring-security-ltpa2](https://github.com/sephiroth-j/spring-security-ltpa2-core). It uses [an embedded LDAP](https://docs.spring.io/spring-boot/docs/1.5.10.RELEASE/reference/htmlsingle/#boot-features-ldap-embedded) as user storage and [EhCache 3](https://www.ehcache.org/) to cache the look-up result with [`@Cacheable`](https://docs.spring.io/spring-boot/docs/1.5.10.RELEASE/reference/htmlsingle/#boot-features-caching).
