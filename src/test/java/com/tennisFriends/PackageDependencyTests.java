package com.tennisFriends;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = App.class)
public class PackageDependencyTests {

    private static final String LESSON = "..modules.lesson..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.ZONE..";
    private static final String MAIN = "..modules.MAIN..";

    // modules 패키지는 modules 패키지만 참조
    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("com.tennisFriends.modules..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("com.tennisFriends.modules..");

    // LESSON 패키지는 EVENT, MAIN, LESSON 패키지에서만 사용되어짐
    @ArchTest
    ArchRule lessonPackageRule = classes().that().resideInAnyPackage(LESSON)
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(EVENT, MAIN, LESSON);

    // EVENT 패키지는 LESSON, ACCOUNT, EVENT 패키지를 사용
    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
            .should().accessClassesThat().resideInAnyPackage(LESSON, ACCOUNT, EVENT);

    // ACCOUNT 패키지는 TAG, ZONE, ACCOUNT 패키지를 사용
    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(TAG, ZONE, ACCOUNT);

    // 각각의 모듈간에 순환참조가 있으면 안된다.
    @ArchTest
    ArchRule cycleCheck = slices().matching("com.tennisFriends.modules.(*)..")
            .should().beFreeOfCycles();

}
