package br.com.caelum.vraptor.validator;

import static br.com.caelum.vraptor.util.Matchers.containValidationMessageFrom;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.core.Localization;

/**
 * A simple class to test JSR303Validator and HibernateValidator3 components.
 *
 * @author Otávio Scherer Garcia
 * @since 3.1.2
 */
public class JSR303ValidatorTest {

	private @Mock Localization localization;

    private JSR303Validator jsr303Validator;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);

    	ValidatorFactoryCreator creator = new ValidatorFactoryCreator();
    	creator.buildFactory();

    	JSR303ValidatorFactory validatorFactory = new JSR303ValidatorFactory(creator.getInstance());
    	validatorFactory.createValidator();

    	MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory(creator.getInstance());
    	interpolatorFactory.createInterpolator();

        this.jsr303Validator = new JSR303Validator(localization, validatorFactory.getInstance(), interpolatorFactory.getInstance());
    }

    @Test
    public void withoutViolations() {
        CustomerJSR303 customer0 = new CustomerJSR303(10, "Vraptor");
        assertTrue(jsr303Validator.validate(customer0).isEmpty());
    }

    @Test
    public void withViolations() {
        CustomerJSR303 customer0 = new CustomerJSR303(null, null);
        assertFalse(jsr303Validator.validate(customer0).isEmpty());
    }
    
    @Test
    public void withSpecificGroup() {
    	Bean bean = new Bean();
    	List<Message> errors = jsr303Validator.validate(bean, Group.class);
    	assertEquals(1, errors.size());
		assertFalse(errors.isEmpty());
		assertThat(errors, containValidationMessageFrom("Name should be not null"));
    }
    
    @Test
    public void usingTwoDifferentsGroups() {
    	Bean bean = new Bean();
    	List<Message> errors = jsr303Validator.validate(bean, Group.class, OtherGroup.class);
    	assertEquals(2, errors.size());
		assertFalse(errors.isEmpty());
		assertThat(errors, containValidationMessageFrom("E-mail should contain @"));
		assertThat(errors, containValidationMessageFrom("Name should be not null"));
    }    
    
    @Test
    public void usingTwoDifferentsGroupsButOnlyOneIsInvalid() {
    	Bean bean = new Bean();
    	bean.setName("Caires");
    	List<Message> errors = jsr303Validator.validate(bean, Group.class, OtherGroup.class);
    	assertEquals(1, errors.size());
		assertFalse(errors.isEmpty());
		assertThat(errors, containValidationMessageFrom("E-mail should contain @"));
		assertThat(errors, not(containValidationMessageFrom("Name should be not null")));
    }
    
    /**
     * Customer for using in bean validator tests.
     */
    public class CustomerJSR303 {

        @javax.validation.constraints.NotNull
        public Integer id;

        @javax.validation.constraints.NotNull
        public String name;

        public CustomerJSR303(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    
    class Bean{
		@NotNull(groups=Group.class, message="Name should be not null")
		private String name = null;
		
		@NotNull(groups=OtherGroup.class, message="E-mail should contain @")
		private String email;
		
		private String address;
		
		public void setAddress(String address) {
			this.address = address;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setEmail(String email) {
			this.email = email;
		}
	}
	
	interface Group{}
	
	interface OtherGroup{}
}
