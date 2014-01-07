/**
 * Copyright (c) 2013 Robert Maupin
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 */
package org.csdgn.hanami;

/**
 * Do not edit this file, it edited by ANT.
 * @author Chase
 */
public class Version {
	public static final String NAME = "Hanami";
	public static final int BUILD = 371;
	public static final int MAJOR = 0;
	public static final int MINOR = 3;
	public static final int PATCH = 5;

	public static final String getVersionString() {
		return String.format("%s %d.%d.%d (build %d)", NAME, MAJOR, MINOR, PATCH, BUILD);
	}
}
