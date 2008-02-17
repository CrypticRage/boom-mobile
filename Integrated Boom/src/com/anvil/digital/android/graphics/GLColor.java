/* 
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anvil.digital.android.graphics;

public class GLColor {

	public float red;
	public float green;
	public float blue;
	public float alpha;
	
	public GLColor(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public GLColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 1.0f;
	} 
	
	/*
	/public int asInt() {
		int temp = 0;
		temp += this.blue;
		temp += (this.green << 8);
		temp += (this.red << 16);
		temp += (this.alpha << 24);
		return temp;
	}
	*/
	
	public boolean equals(Object other) {
		if (other instanceof GLColor) {
			GLColor color = (GLColor)other;
			return (red == color.red && green == color.green &&
					blue == color.blue && alpha == color.alpha);
		}
		return false;
	}
}
