/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.jaxb22.plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.xml.sax.ErrorHandler;

import com.helger.commons.annotations.CodingStyleguideUnaware;
import com.helger.commons.annotations.IsSPIImplementation;
import com.helger.commons.collections.ContainerHelper;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

/**
 * Create {@link CodingStyleguideUnaware} annotations in all bean generated
 * classes as well as in the ObjectFactory classes
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public class PluginCodingStyleguideUnaware extends Plugin
{
  private static final String OPT = "Xph-csu";

  @Override
  public String getOptionName ()
  {
    return OPT;
  }

  @Override
  public String getUsage ()
  {
    return "  -" + OPT + "       :  add CodingStyleguideUnaware annotations to all classes";
  }

  @Override
  public List <String> getCustomizationURIs ()
  {
    return ContainerHelper.newUnmodifiableList (CJAXB22.NSURI_PH);
  }

  @Override
  public boolean run (@Nonnull final Outline aOutline,
                      @Nonnull final Options aOpts,
                      @Nonnull final ErrorHandler aErrorHandler)
  {
    // For all classes
    for (final ClassOutline aClassOutline : aOutline.getClasses ())
    {
      final JDefinedClass jClass = aClassOutline.implClass;
      jClass.annotate (CodingStyleguideUnaware.class);
    }

    // Get all unique ObjectFactory classes
    final Set <JDefinedClass> aObjFactories = new HashSet <JDefinedClass> ();
    for (final CElementInfo ei : aOutline.getModel ().getAllElements ())
    {
      final JDefinedClass aClass = aOutline.getPackageContext (ei._package ())
                                           .objectFactoryGenerator ()
                                           .getObjectFactory ();
      aObjFactories.add (aClass);
    }

    // Manipulate all ObjectFactory classes
    for (final JDefinedClass aObjFactory : aObjFactories)
      aObjFactory.annotate (CodingStyleguideUnaware.class);

    return true;
  }
}