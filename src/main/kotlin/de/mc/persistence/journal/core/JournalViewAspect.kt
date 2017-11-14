package de.mc.persistence.journal.core

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * ralfulrich on 11.03.17.
 */
@Aspect
@Component
open class JournalViewAspect {

    val threadName = "viewThread"
    val viewExecutor = Executors.newSingleThreadExecutor({ Thread(it, threadName) })

    @Pointcut("within(de.mc.persistence.journal.core.JournalView+)")
    private fun allJournalViewClasses() {
    }

    @Pointcut("execution(public * *(..))")
    private fun allPublicMethods() {
    }

    @Around("allPublicMethods() && allJournalViewClasses()")
    private fun allJournalViewMethods(pjp: ProceedingJoinPoint): Any? {
        return if (Thread.currentThread().name == threadName)
            pjp.proceed()
        else
            viewExecutor.submit<Any?> { pjp.proceed() }.get(60, TimeUnit.SECONDS)
    }
}