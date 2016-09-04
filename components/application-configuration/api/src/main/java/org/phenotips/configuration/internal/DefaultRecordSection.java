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

import org.phenotips.configuration.RecordElement;
import org.phenotips.configuration.RecordSection;

import org.xwiki.uiextension.UIExtension;
import org.xwiki.uiextension.UIExtensionFilter;
import org.xwiki.uiextension.UIExtensionManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Default (global) implementation for {@link RecordSection}.
 *
 * @version $Id$
 * @since 1.3M3
 */
public class DefaultRecordSection implements RecordSection
{
    /** @see #getExtension() */
    protected final UIExtension extension;

    /** Lists the contained fields. */
    protected final UIExtensionManager uixManager;

    /** Sorts fields by their declared order. */
    protected final UIExtensionFilter orderFilter;

    protected boolean enabled = true;

    protected boolean expanded;

    protected List<RecordElement> elements;

    /**
     * Simple constructor passing all the needed components.
     *
     * @param extension the extension defining this element
     * @param uixManager the UIExtension manager
     * @param orderFilter UIExtension filter for ordering sections and elements
     */
    public DefaultRecordSection(UIExtension extension, UIExtensionManager uixManager, UIExtensionFilter orderFilter)
    {
        this.extension = extension;
        this.uixManager = uixManager;
        this.orderFilter = orderFilter;
        if (extension != null) {
            this.enabled = !StringUtils.equals("false", extension.getParameters().get("enabled"));
        }
        this.expanded = isExpandedByDefault();
    }

    @Override
    public UIExtension getExtension()
    {
        return this.extension;
    }

    @Override
    public String getName()
    {
        String result = this.extension.getParameters().get("title");
        if (StringUtils.isBlank(result)) {
            result = StringUtils.capitalize(StringUtils.replaceChars(
                StringUtils.substringAfterLast(this.extension.getId(), "."), "_-", "  "));
        }
        return result;
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }

    @Override
    public boolean isExpandedByDefault()
    {
        if (this.extension != null) {
            return StringUtils.equals("true", this.extension.getParameters().get("expanded_by_default"));
        }
        return false;
    }

    @Override
    public List<RecordElement> getAllElements()
    {
        List<RecordElement> result = new LinkedList<>();
        List<UIExtension> fields = this.uixManager.get(this.extension.getId());
        fields = this.orderFilter.filter(fields, "order");
        for (UIExtension field : fields) {
            result.add(new DefaultRecordElement(field, this));
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<RecordElement> getEnabledElements()
    {
        // FIX ME
        if (this.elements != null) {
            return this.elements;
        }
        List<RecordElement> result = new LinkedList<>();
        for (RecordElement element : getAllElements()) {
            if (element.isEnabled()) {
                result.add(element);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder(getName());
        result.append(" [");
        result.append(StringUtils.join(getEnabledElements(), ", "));
        result.append(']');
        return result.toString();
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public void setExpandedByDefault(boolean expanded)
    {
        this.expanded = expanded;
    }

    @Override
    public void setElements(List<RecordElement> elements)
    {
        this.elements = elements;
    }
}
