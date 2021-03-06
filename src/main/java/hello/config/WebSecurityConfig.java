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
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.DefaultLdapUsernameToDnMapper;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.LdapUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableCaching
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http
			.authorizeRequests()
				.antMatchers("/", "/home").permitAll()
				.antMatchers("/hello").hasRole("DEVELOPERS")
				.and()
			.apply(new Ltpa2Configurer())
				.sharedKey(sharedKey())
				.signerKey(signerKey())
			;
		http.userDetailsService(userDetailsService());
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService()
	{
		final DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource("ldap://127.0.0.1:33389/dc=foo,dc=bar");
		contextSource.afterPropertiesSet();

		LdapUserDetailsManager manager = new MyLdapUserDetailsManager(contextSource);
		manager.setUsernameMapper(new DefaultLdapUsernameToDnMapper("ou=user", "cn"));
		manager.setGroupSearchBase("ou=groups");
		return manager;
	}

	private SecretKey sharedKey() throws GeneralSecurityException
	{
		String testKey = "JvywHhxC+EhtUdeusbo31E5IUOEPmbMxMnKTTOB39fo=";
		String testKeyPass = "test123";
		return LtpaKeyUtils.decryptSharedKey(testKey, testKeyPass);
	}

	private PublicKey signerKey() throws GeneralSecurityException
	{
		String testSignerKey = "AOECPMDAs0o7MzQIgxZhAXJZ2BaDE3mqRZAbkbQO38CgUIgeAPEA3iWIYp+p/Ai0J4//UOml20an+AuCnDGzcFCaf3S3EAiR4cK59vl/u8TIswPIg2akh4J7qL3E/qRxN9WD945tS3h0YhJZSq7rC22wytLsxbFuKpEuYfm1i5spAQAB";
		return LtpaKeyUtils.decodePublicKey(testSignerKey);
	}
}
