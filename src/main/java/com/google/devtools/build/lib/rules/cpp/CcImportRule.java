// Copyright 2017 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.rules.cpp;

import static com.google.devtools.build.lib.packages.Attribute.ConfigurationTransition.DATA;
import static com.google.devtools.build.lib.packages.Attribute.attr;
import static com.google.devtools.build.lib.packages.BuildType.LABEL;
import static com.google.devtools.build.lib.packages.BuildType.LABEL_LIST;
import static com.google.devtools.build.lib.syntax.Type.BOOLEAN;

import com.google.devtools.build.lib.analysis.BaseRuleClasses;
import com.google.devtools.build.lib.analysis.RuleDefinition;
import com.google.devtools.build.lib.analysis.RuleDefinitionEnvironment;
import com.google.devtools.build.lib.packages.RuleClass;
import com.google.devtools.build.lib.packages.RuleClass.Builder;
import com.google.devtools.build.lib.packages.RuleClass.Builder.RuleClassType;
import com.google.devtools.build.lib.util.FileTypeSet;

/** Rule definition for the cc_import rule. */
public final class CcImportRule implements RuleDefinition {
  @Override
  public RuleClass build(Builder builder, RuleDefinitionEnvironment env) {
    return builder
        /*<!-- #BLAZE_RULE($cc_import).ATTRIBUTE(static_library) -->
          A single precompiled static library.
          Permited file types: <code>.a</code>, <code>.pic.a</code> or <code>.lib</code>
        <!-- #END_BLAZE_RULE.ATTRIBUTE -->*/
        .add(
            attr("static_library", LABEL)
                .allowedFileTypes(CppFileTypes.ARCHIVE, CppFileTypes.PIC_ARCHIVE)
        )
        /*<!-- #BLAZE_RULE($cc_import).ATTRIBUTE(shared_library) -->
          A single precompiled shared library
          Permited file types: <code>.so</code>, <code>.dll</code> or <code>.dylib</code>
        <!-- #END_BLAZE_RULE.ATTRIBUTE -->*/
        .add(attr("shared_library", LABEL)
            .allowedFileTypes(CppFileTypes.SHARED_LIBRARY)
        )
        /*<!-- #BLAZE_RULE($cc_import).ATTRIBUTE(shared_library) -->
          A single interface library for linking the shared library
          Permited file types: <code>.ifso</code>, <code>.tbd</code>, <code>.so</code> or
          <code>.dylib</code>
        <!-- #END_BLAZE_RULE.ATTRIBUTE -->*/
        .add(attr("interface_library", LABEL)
            .allowedFileTypes(
                CppFileTypes.INTERFACE_SHARED_LIBRARY,
                CppFileTypes.UNIX_SHARED_LIBRARY))
        /*<!-- #BLAZE_RULE($cc_import).ATTRIBUTE(hdrs) -->
          The list of header files published by
          this precompiled library to be directly included by sources in dependent rules.
        <!-- #END_BLAZE_RULE.ATTRIBUTE -->*/
        .add(
            attr("hdrs", LABEL_LIST)
                .orderIndependent()
                .direct_compile_time_input()
                .allowedFileTypes(CppFileTypes.CPP_HEADER))
        /*<!-- #BLAZE_RULE(cc_import).ATTRIBUTE(system_provide) -->
        If true, it indicates the shared library required at runtime is provided by the system. In
        this case, <code>interface_library</code> should be specified and
        <code>shared_library</code> should be empty.
        <!-- #END_BLAZE_RULE.ATTRIBUTE -->*/
        .add(
            attr("system_provided", BOOLEAN))
        /*<!-- #BLAZE_RULE(cc_import).ATTRIBUTE(alwayslink) -->
        If 1, any binary that depends (directly or indirectly) on this C++
        precompiled library will link in all the object files archived in the static library,
        even if some contain no symbols referenced by the binary.
        This is useful if your code isn't explicitly called by code in
        the binary, e.g., if your code registers to receive some callback
        provided by some service.
        <!-- #END_BLAZE_RULE.ATTRIBUTE -->*/
        .add(
            attr("alwayslink", BOOLEAN))
        .add(attr("data", LABEL_LIST).cfg(DATA)
            .allowedFileTypes(FileTypeSet.ANY_FILE)
            .dontCheckConstraints())
        .build();
  }

  @Override
  public  Metadata getMetadata() {
    return RuleDefinition.Metadata.builder()
        .name("$cc_import")
        .ancestors(BaseRuleClasses.BaseRule.class)
        .type(RuleClassType.ABSTRACT)
        .build();
  }
}
