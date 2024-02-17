package ru.FedorILyaCO.MLTests.application.pyExecution;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class PythonExecutorTest {
    @Test
    public void Hello(){
        assertEquals("Hi", "Hi");
    }
    @Test
    public void Hello2(){
        assertEquals("Hello", "Hi");
    }

    @Test
    public void Hello3(){
        assertEquals("Hello3", "Hi");
    }

    private void printHello(){
        System.out.println("Hello");
    }
}