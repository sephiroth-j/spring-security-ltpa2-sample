/*
 * Copyright 2018 Ronny "Sephiroth" Perinke <sephiroth@sephiroth-j.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hello.config;

import de.sephirothj.spring.security.ltpa2.Ltpa2Configurer;
import de.sephirothj.spring.security.ltpa2.LtpaKeyUtils;
import hello.auth.MyLdapUserDetailsManager;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.DefaultLdapUsernameToDnMapper;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.LdapUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableCaching
@EnableMethodSecurity(jsr250Enabled = true)
@Slf4j
public class WebSecurityConfig
{

	@Bean
	public SecurityFilterChain ltpa2SecurityFilterChain(final HttpSecurity http, final UserDetailsService userDetailsService) throws Exception
	{
		http
			.authorizeHttpRequests(authorize -> authorize
			.requestMatchers("/", "/home").permitAll()
			.requestMatchers("/hello").hasRole("DEVELOPERS")
			// all other require any authentication
			.anyRequest().authenticated()
			)
			.with(new Ltpa2Configurer(),
				ltpa2Configurer -> ltpa2Configurer
					.sharedKey(sharedKey())
					.signerKey(signerKey())
					// define custom failure handler to demonstrate changing the response code
					.authFailureHandler((request, response, exception) -> response.sendError(HttpStatus.I_AM_A_TEAPOT.value(), "I am a teapot!")));
		http.userDetailsService(userDetailsService);
		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService()
	{
		final DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource("ldap://127.0.0.1:33389/dc=foo,dc=bar");
		contextSource.afterPropertiesSet();

		LdapUserDetailsManager manager = new MyLdapUserDetailsManager(contextSource);
		manager.setUsernameMapper(new DefaultLdapUsernameToDnMapper("ou=user", "cn"));
		manager.setGroupSearchBase("ou=groups");
		return manager;
	}

	private SecretKey sharedKey()
	{
		String testKey = "JvywHhxC+EhtUdeusbo31E5IUOEPmbMxMnKTTOB39fo=";
		String testKeyPass = "test123";
		try
		{
			return LtpaKeyUtils.decryptSharedKey(testKey, testKeyPass);
		}
		catch (GeneralSecurityException ex)
		{
			log.error(ex.getLocalizedMessage());
			return null;
		}
	}

	private PublicKey signerKey()
	{
		String testSignerKey = "AOECPMDAs0o7MzQIgxZhAXJZ2BaDE3mqRZAbkbQO38CgUIgeAPEA3iWIYp+p/Ai0J4//UOml20an+AuCnDGzcFCaf3S3EAiR4cK59vl/u8TIswPIg2akh4J7qL3E/qRxN9WD945tS3h0YhJZSq7rC22wytLsxbFuKpEuYfm1i5spAQAB";
		try
		{
			return LtpaKeyUtils.decodePublicKey(testSignerKey);
		}
		catch (GeneralSecurityException ex)
		{
			log.error(ex.getLocalizedMessage());
			return null;
		}
	}
}
