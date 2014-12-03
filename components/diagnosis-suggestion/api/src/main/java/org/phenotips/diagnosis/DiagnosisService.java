/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.phenotips.diagnosis;

import org.phenotips.ontology.OntologyTerm;

import org.xwiki.component.annotation.Role;

import java.util.List;

/**
 * @since 1.1M1
 * @version $Id$
 */
@Role
public interface DiagnosisService
{
    /**
     * Get a list of suggest diagnosies given a list of present phenotypes. Each phenotype is represented as a String
     * in the format {@code <ontology prefix>:<term id>}, for example
     *            {@code HP:0002066}
     *
     * @param phenotypes A List of String phenotypes observed in the patient
     * @param limit a number of phenotypes to return
     * @return A list of suggested diagnosies
     */
    List<OntologyTerm> getDiagnosis(List<String> phenotypes, int limit);
}
