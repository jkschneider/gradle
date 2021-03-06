## New and noteworthy

Here are the new features introduced in this Gradle release.

### CUnit integration (i)

The new Gradle `cunit` plugin provides support for compiling and executing CUnit tests in your native-binary project.

You simply need to include your CUnit test sources, and provide a hook for Gradle to register the suites and tests defined.

    #include <CUnit/Basic.h>
    #include "gradle_cunit_register.h"
    #include "operator_tests.h"

    void gradle_cunit_register() {
        CU_pSuite mySuite = CU_add_suite("operator tests", suite_init, suite_clean);
        CU_add_test(mySuite, "test plus", test_plus);
        CU_add_test(mySuite, "test minus", test_minus);
    }

Gradle will then generate the required boiler-plate CUnit code, build a test executable, and run your tests.

    > gradle -q runFailingOperatorsTest

    There were test failures:
      1. /home/user/gradle/samples/native-binaries/cunit/src/operatorsTest/cunit/test_plus.c:6  - plus(0, -2) == -2
      2. /home/user/gradle/samples/native-binaries/cunit/src/operatorsTest/cunit/test_plus.c:7  - plus(2, 2) == 4

    BUILD FAILED


See the [user guide chapter](docs/userguide/nativeBinaries.html#native_binaries:cunit) and the cunit sample (`samples/native-binaries/cunit`)
in the distribution to learn more. Expect deeper integration with CUnit (and other native testing tools) in the future.

### Component metadata rules can control whether a component version is considered changing

Component metadata rules can now control whether a component version is considered changing, or in other words, whether the contents
of one and the same component version may change over time. (A common example for a changing component version is a Maven snapshot dependency.)
This makes it possible to implement custom strategies for deciding if a component version is changing. In the following example, every
component version whose group is `my.company` and whose version number ends in `-dev` will be considered changing:

    dependencies {
        components {
            eachComponent { ComponentMetadataDetails details ->
                details.changing =
                    details.id.group == "my.company" &&
                        details.id.version.endsWith("-dev")
            }
        }
    }

This feature is especially useful when dealing with Ivy repositories, as it is a generalized form of Ivy's `changingPattern` concept.

### Tooling API exposes information on a project's publications

Tooling API clients can now get basic information on a project's publications:

    def projectConnection = ...
    def project = projectConnection.getModel(GradleProject)
    for (publication in project.publications) {
        println publication.id.group
        println publication.id.name
        println publication.id.version
    }

Both publications declared in the old (`artifacts` block, `Upload` task) and new (`publishing.publications` block) way are reflected in the result.

## Promoted features

Promoted features are features that were incubating in previous versions of Gradle but are now supported and subject to backwards compatibility.
See the User guide section on the “[Feature Lifecycle](userguide/feature_lifecycle.html)” for more information.

The following are the features that have been promoted in this Gradle release.

<!--
### Example promoted
-->

## Fixed issues

## Deprecations

Features that have become superseded or irrelevant due to the natural evolution of Gradle become *deprecated*, and scheduled to be removed
in the next major Gradle version (Gradle 2.0). See the User guide section on the “[Feature Lifecycle](userguide/feature_lifecycle.html)” for more information.

The following are the newly deprecated items in this Gradle release. If you have concerns about a deprecation, please raise it via the [Gradle Forums](http://forums.gradle.org).

### Deprecations in Tooling API communication

* Using Tooling API to connect to provider using older distribution than Gradle 1.0-milestone-8 is now deprecated and scheduled for removal in version Gradle 2.0.
* Using Tooling API client version older than 1.2 to connect to a provider from current distribution is now deprecated and scheduled for removal in version Gradle 2.0.
<!--
### Example deprecation
-->

## Potential breaking changes

### Changes to incubating native support

* '-Xlinker' is no longer automatically added to linker args for GCC or Clang. If you want to pass an argument directly to 'ld' you need to add this escape yourself.
* Tasks for windows resource compilation are now named 'compileXXXX' instead of 'resourceCompileXXX'.

### Change to treatment of poms with packaging 'pom'

If you have a dependency who's pom has packaging of 'pom', Gradle now expects that there will always be an associated jar artifact and will fail to resolve if there is not.
In particular, this means that you can not have a dependency with a pom that only declares further dependencies and has no artifacts itself.

### Change to JUnit XML file for skipped tests

The way that skipped/ignored tests are represented in the JUnit XML output file produced by the `Test` task.
Gradle now produces the same output, with regard to skipped tests, as Apache Ant and Apache Maven.
This format is accepted, and expected, by all major Continuous Integration servers.

This change is described as follows:

1. The `testsuite` element now contains a `skipped` attribute, indicating the number of skipped tests (may be 0)
2. The element representing a test case is now always named `testcase` (previously it was named `ignored-testcase` if it was a skipped test)
3. If a test case was skipped, a child `<skipped/>` element will be present

No changes are necessary to build scripts or Continuous Integration server configuration to accommodate this change.

### Ordering of dependencies in imported Ant builds

The ordering of Ant target dependencies is now respected when possible.
This may cause tasks of imported Ant builds to executed in a different order from this version of Gradle on.

Given…

    <target name='a' depends='d,c,b'/>

A shouldRunAfter [task ordering](userguide/more_about_tasks.html#sec:ordering_tasks) will be applied to the dependencies so that,
`c.shouldRunAfter d` and `b.shouldRunAfter c`.

This is in alignment with Ant's ordering of target dependencies.

## External contributions

We would like to thank the following community members for making contributions to this release of Gradle.

* [Jesse Glick](https://github.com/jglick) - enabling newlines in option values passed to build
* [Zeeke](https://github.com/zeeke) - documentation improvements
* [Kamil Szymański](https://github.com/kamilszymanski) - documentation improvements
* [Jakub Kubryński](https://github.com/jkubrynski) - handling of empty string proxy system property values
* [Lee Symes](https://github.com/leesdolphin) & [Greg Temchenko](https://github.com/soid) - fix skipped test representation in JUnit XML result files [GRADLE-2731]
* [Ivan Vyshnevskyi](https://github.com/sainaen) - Fixes to HTML test report
* [Marcin Erdmann](https://github.com/erdi) - dependency ordering of imported ant targets [GRADLE-1102]
* [Vincent Cantin](https://github.com/green-coder) - documentation improvements
* [Sterling Greene](https://github.com/big-guy) - Support for developing Gradle in Eclipse
* [Matthew Michihara](https://github.com/matthewmichihara) - Documentation improvements
* [Andrew Oberstar](https://github.com/ajoberstar) - Improved Sonar support on Java 1.5 [GRADLE-3005]

We love getting contributions from the Gradle community. For information on contributing, please see [gradle.org/contribute](http://gradle.org/contribute).

## Known issues

Known issues are problems that were discovered post release that are directly related to changes made in this release.
