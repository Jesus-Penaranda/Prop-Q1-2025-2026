package Tests.Unitarios;

import dominio.Persona;
import org.junit.Test;
import static org.junit.Assert.*;

public class PersonaTest 
{

    @Test
    public void testDefaultConstructor_hasEmptyId() 
    {
        Persona p = new Persona();
        assertNotNull(p.getId());
        assertEquals("", p.getId());
    }

    @Test
    public void testParamConstructor_returnsGivenId() 
    {
        Persona p = new Persona("usuario123");
        assertEquals("usuario123", p.getId());
    }

    @Test
    public void testCopyConstructor_copiesIdIndependently() 
    {
        Persona original = new Persona("orig");
        Persona copia = new Persona(original);
        assertEquals("orig", copia.getId());
        Persona otro = new Persona("nuevo");
        assertEquals("orig", copia.getId());
        assertEquals("nuevo", otro.getId());
    }
}