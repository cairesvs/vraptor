package br.com.caelum.vraptor.util;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.com.caelum.vraptor.validator.Message;

public class Matchers {

	public static TypeSafeMatcher<List<Message>> containValidationMessageFrom(final String message){
		return new TypeSafeMatcher<List<Message>>() {


			@Override
			protected boolean matchesSafely(List<Message> messages) {
				for (Message actual : messages) {
					if(message.equals(actual.getMessage())) {
						return true;
					}
				}
				return false;
			}

			public void describeTo(Description description) {}

			@Override
			protected void describeMismatchSafely(List<Message> item,
					Description mismatchDescription) {}

		};
		
	}
	
}
