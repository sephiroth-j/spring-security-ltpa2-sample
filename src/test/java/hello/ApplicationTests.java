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
package hello;

import de.sephirothj.spring.security.ltpa2.Ltpa2Token;
import de.sephirothj.spring.security.ltpa2.Ltpa2Utils;
import de.sephirothj.spring.security.ltpa2.LtpaKeyUtils;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests
{
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void accessUnsecuredResourceThenOk() throws Exception
	{
		mockMvc.perform(get("/"))
			.andExpect(status().isOk());
	}

	@Test
	public void accessSecuredResourceUnauthenticatedShouldBeForbidden() throws Exception
	{
		mockMvc.perform(get("/hello"))
			.andExpect(status().isForbidden());
	}

	@Test
	public void accessSecuredResourceWithInvalidAuthenticationShouldBeForbidden() throws Exception
	{
		mockMvc.perform(get("/hello").header("Authorization", "sadfdas"))
			.andExpect(status().isForbidden());
	}

	@Test
	public void accessSecuredResourceWithAuthenticationThenOk() throws Exception
	{
		Ltpa2Token token = createTestToken();
		
		mockMvc.perform(get("/hello").header("Authorization", "LtpaToken2 ".concat(encryptToken(token))))
			.andExpect(status().isOk());
	}

	@Test
	public void accessSecuredResourceWithCookieThenOk() throws Exception
	{
		Ltpa2Token token = createTestToken();
		
		Cookie ltpaCookie = new Cookie("LtpaToken2", encryptToken(token));
		mockMvc.perform(get("/secured-method").cookie(ltpaCookie))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = "DEVELOPERS")
	public void accessSecuredResourceAuthenticatedThenOk() throws Exception
	{
		mockMvc.perform(get("/hello"))
			.andExpect(status().isOk());
	}

	private Ltpa2Token createTestToken()
	{
		Ltpa2Token token = new Ltpa2Token();
		token.setUser("user:LdapRegistry/CN=fae6d87c-c642-45a6-9f09-915c7fd8b08c,OU=user,DC=foo,DC=bar");
		token.setExpire(LocalDateTime.now().plusMinutes(1));
		return token;
	}
	
	private String encryptToken(@NonNull Ltpa2Token token) throws GeneralSecurityException
	{
		SecretKey sharedKey = LtpaKeyUtils.decryptSharedKey(Constants.ENCRYPTED_SHARED_KEY, Constants.ENCRYPTION_PASSWORD);
		PrivateKey privateKey = LtpaKeyUtils.decryptPrivateKey(Constants.ENCRYPTED_PRIVATE_KEY, Constants.ENCRYPTION_PASSWORD);

		return Ltpa2Utils.encryptToken(token, privateKey, sharedKey);
	}
}
