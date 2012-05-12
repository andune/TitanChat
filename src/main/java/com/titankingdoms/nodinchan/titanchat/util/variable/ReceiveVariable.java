package com.titankingdoms.nodinchan.titanchat.util.variable;

import java.util.ArrayList;
import java.util.List;

import com.titankingdoms.nodinchan.titanchat.util.variable.Variable.IVariable;

/*     Copyright (C) 2012  Nodin Chan <nodinchan@live.com>
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public abstract class ReceiveVariable implements IVariable {
	
	private static final List<ReceiveVariable> variables = new ArrayList<ReceiveVariable>();
	
	public static final List<IVariable> getVariables() {
		return new ArrayList<IVariable>(variables);
	}
	
	public static final void register(ReceiveVariable variable) {
		variables.add(variable);
	}

	public final String replace(String line) {
		return line.replace(getVariable(), getReplacement());
	}
	
	public static final void unload() {
		variables.clear();
	}
}