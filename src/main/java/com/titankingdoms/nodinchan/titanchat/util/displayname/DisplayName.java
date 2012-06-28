package com.titankingdoms.nodinchan.titanchat.util.displayname;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;

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

@Entity
@Table(name = "display_Names")
public class DisplayName {
	
	@Id
	@GeneratedValue
	private int id;
	
	@NotEmpty
	private String DisplayName;
	
	@NotEmpty
	private String Name;
	
	public String getDisplayName() {
		return DisplayName;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return Name;
	}
	
	public void setDisplayName(String DisplayName) {
		this.DisplayName = DisplayName;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String Name) {
		this.Name = Name;
	}
}