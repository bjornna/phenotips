/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
package org.phenotips.configuration.internal;

import org.phenotips.configuration.RecordConfiguration;
import org.phenotips.configuration.RecordConfigurationManager;
import org.phenotips.configuration.RecordConfigurationModule;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.util.ReflectionUtils;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Provider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Tests for the default {@link RecordConfigurationManager} implementation, {@link DefaultRecordConfigurationManager}.
 *
 * @version $Id$
 */
public class DefaultRecordConfigurationManagerTest
{
    @Rule
    public final MockitoComponentMockingRule<RecordConfigurationManager> mocker =
        new MockitoComponentMockingRule<RecordConfigurationManager>(
            DefaultRecordConfigurationManager.class);

    @Mock
    private XWikiContext context;

    @Mock
    private XWiki xwiki;

    @Mock
    private RecordConfiguration config;

    @Mock
    private RecordConfigurationModule moduleOne;

    @Mock
    private RecordConfigurationModule moduleTwo;

    @Mock
    private RecordConfigurationModule moduleThree;

    @Mock
    private Provider<List<RecordConfigurationModule>> modules;

    private List<RecordConfigurationModule> moduleList;

    @Before
    public void setup() throws ComponentLookupException
    {
        MockitoAnnotations.initMocks(this);
        resetMocks();
        ReflectionUtils.setFieldValue(this.mocker.getComponentUnderTest(), "modules", this.modules);
    }

    @Test
    public void defaultDecisionIsDeny() throws ComponentLookupException
    {
        doReturn(new LinkedList<>()).when(this.modules).get();
        Assert.assertNotEquals(this.config, this.mocker.getComponentUnderTest().getConfiguration(""));
    }

    @Test
    public void moduleOutputIsUsed() throws Exception
    {
        this.moduleList = Collections.singletonList(this.moduleOne);
        doReturn(this.moduleList).when(this.modules).get();

        when(this.moduleOne.process(any(RecordConfiguration.class))).thenReturn(null);
        Assert.assertNull(this.mocker.getComponentUnderTest().getConfiguration(""));

        when(this.moduleOne.process(any(RecordConfiguration.class))).thenReturn(this.config);
        Assert.assertSame(this.config, this.mocker.getComponentUnderTest().getConfiguration(""));

    }

    @Test
    public void modulesAreCascaded() throws Exception
    {
        this.moduleList = Arrays.asList(this.moduleOne, this.moduleTwo, this.moduleThree);
        doReturn(this.moduleList).when(this.modules).get();
        when(this.moduleOne.process(any(RecordConfiguration.class))).thenReturn(this.config);
        when(this.moduleTwo.process(this.config)).thenReturn(null);
        when(this.moduleThree.process(null)).thenReturn(this.config);

        Assert.assertSame(this.config, this.mocker.getComponentUnderTest().getConfiguration(""));
        InOrder order = Mockito.inOrder(this.moduleOne, this.moduleTwo, this.moduleThree);
        order.verify(this.moduleOne).process(any(RecordConfiguration.class));
        order.verify(this.moduleTwo).process(any(RecordConfiguration.class));
        order.verify(this.moduleThree).process(any(RecordConfiguration.class));
    }

    @Test
    public void allModulesAreInvoked() throws Exception
    {
        this.moduleList = Arrays.asList(this.moduleOne, this.moduleTwo);
        doReturn(this.moduleList).when(this.modules).get();

        when(this.moduleOne.process(any(RecordConfiguration.class))).thenReturn(this.config);
        when(this.moduleTwo.process(this.config)).thenReturn(null);

        Assert.assertNull(this.mocker.getComponentUnderTest().getConfiguration(""));

        when(this.moduleOne.process(any(RecordConfiguration.class))).thenReturn(null);
        when(this.moduleTwo.process(null)).thenReturn(this.config);

        Assert.assertSame(this.config, this.mocker.getComponentUnderTest().getConfiguration(""));
    }

    @Test
    public void exceptionsInModulesAreIgnored() throws Exception
    {
        this.moduleList = Arrays.asList(this.moduleOne, this.moduleTwo);
        doReturn(this.moduleList).when(this.modules).get();

        when(this.moduleOne.process(any(RecordConfiguration.class))).thenThrow(new NullPointerException());
        when(this.moduleTwo.process(any(RecordConfiguration.class))).thenReturn(this.config);
        Assert.assertSame(this.config, this.mocker.getComponentUnderTest().getConfiguration(""));
    }

    private void resetMocks()
    {
        Mockito.reset(this.moduleOne, this.moduleTwo, this.moduleThree);
        when(this.moduleOne.process(this.config)).thenReturn(null);
        when(this.moduleTwo.process(this.config)).thenReturn(null);
        when(this.moduleThree.process(this.config)).thenReturn(null);
    }

}
