/* Generated By:JavaCC: Do not edit this line. MedalConstants.java */
/*
 * Copyright 2008 Philip van Oosten (Mentoring Systems BVBA)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */

package longbow.metadata;

public interface MedalConstants {

  int EOF = 0;
  int WHITE = 5;
  int LETTER = 6;
  int LEFT_BRACKET = 7;
  int RIGHT_BRACKET = 8;
  int AND = 9;
  int OR = 10;
  int NAND = 11;
  int NOR = 12;
  int XOR = 13;
  int NOT = 14;
  int ACCEPT_ALL = 15;
  int ACCEPT_NONE = 16;
  int TYPE_IS = 17;
  int NOT_NULL = 18;
  int CLASSNAME = 19;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\t\"",
    "<WHITE>",
    "<LETTER>",
    "\"(\"",
    "\")\"",
    "\"AND\"",
    "\"OR\"",
    "\"NAND\"",
    "\"NOR\"",
    "\"XOR\"",
    "\"NOT\"",
    "<ACCEPT_ALL>",
    "<ACCEPT_NONE>",
    "<TYPE_IS>",
    "<NOT_NULL>",
    "<CLASSNAME>",
  };

}