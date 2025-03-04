/*
 * Copyright (C) 2009 The Android Open Source Project
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
package com.android.vcard.tests;

import android.content.ContentValues;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.SipAddress;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;

import com.android.vcard.VCardConfig;
import com.android.vcard.tests.testutils.ContentValuesVerifier;
import com.android.vcard.tests.testutils.ContentValuesVerifierElem;
import com.android.vcard.tests.testutils.PropertyNodesVerifierElem.TypeSet;
import com.android.vcard.tests.testutils.VCardTestsBase;

import java.util.Arrays;

public class VCardImporterTests extends VCardTestsBase {
    // Push data into int array at first since values like 0x80 are
    // interpreted as int by the compiler and casting all of them is
    // cumbersome...
    private static final int[] sPhotoIntArrayForComplicatedCase = {
        0xff, 0xd8, 0xff, 0xe1, 0x0a, 0x0f, 0x45, 0x78, 0x69, 0x66, 0x00,
        0x00, 0x4d, 0x4d, 0x00, 0x2a, 0x00, 0x00, 0x00, 0x08, 0x00, 0x0d,
        0x01, 0x0e, 0x00, 0x02, 0x00, 0x00, 0x00, 0x0f, 0x00, 0x00, 0x00,
        0xaa, 0x01, 0x0f, 0x00, 0x02, 0x00, 0x00, 0x00, 0x07, 0x00, 0x00,
        0x00, 0xba, 0x01, 0x10, 0x00, 0x02, 0x00, 0x00, 0x00, 0x06, 0x00,
        0x00, 0x00, 0xc2, 0x01, 0x12, 0x00, 0x03, 0x00, 0x00, 0x00, 0x01,
        0x00, 0x01, 0x00, 0x00, 0x01, 0x1a, 0x00, 0x05, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0xc8, 0x01, 0x1b, 0x00, 0x05, 0x00, 0x00,
        0x00, 0x01, 0x00, 0x00, 0x00, 0xd0, 0x01, 0x28, 0x00, 0x03, 0x00,
        0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x00, 0x01, 0x31, 0x00, 0x02,
        0x00, 0x00, 0x00, 0x0e, 0x00, 0x00, 0x00, 0xd8, 0x01, 0x32, 0x00,
        0x02, 0x00, 0x00, 0x00, 0x14, 0x00, 0x00, 0x00, 0xe6, 0x02, 0x13,
        0x00, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x82,
        0x98, 0x00, 0x02, 0x00, 0x00, 0x00, 0x0e, 0x00, 0x00, 0x00, 0xfa,
        0x87, 0x69, 0x00, 0x04, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x01,
        0x84, 0xc4, 0xa5, 0x00, 0x07, 0x00, 0x00, 0x00, 0x7c, 0x00, 0x00,
        0x01, 0x08, 0x00, 0x00, 0x04, 0x1e, 0x32, 0x30, 0x30, 0x38, 0x31,
        0x30, 0x32, 0x39, 0x31, 0x33, 0x35, 0x35, 0x33, 0x31, 0x00, 0x00,
        0x44, 0x6f, 0x43, 0x6f, 0x4d, 0x6f, 0x00, 0x00, 0x44, 0x39, 0x30,
        0x35, 0x69, 0x00, 0x00, 0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x01,
        0x00, 0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x01, 0x44, 0x39, 0x30,
        0x35, 0x69, 0x20, 0x56, 0x65, 0x72, 0x31, 0x2e, 0x30, 0x30, 0x00,
        0x32, 0x30, 0x30, 0x38, 0x3a, 0x31, 0x30, 0x3a, 0x32, 0x39, 0x20,
        0x31, 0x33, 0x3a, 0x35, 0x35, 0x3a, 0x34, 0x37, 0x00, 0x20, 0x20,
        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
        0x00, 0x50, 0x72, 0x69, 0x6e, 0x74, 0x49, 0x4d, 0x00, 0x30, 0x33,
        0x30, 0x30, 0x00, 0x00, 0x00, 0x06, 0x00, 0x01, 0x00, 0x14, 0x00,
        0x14, 0x00, 0x02, 0x01, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x00,
        0x00, 0x34, 0x01, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01,
        0x00, 0x00, 0x00, 0x01, 0x10, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x11, 0x09, 0x00, 0x00, 0x27, 0x10, 0x00, 0x00, 0x0f, 0x0b, 0x00,
        0x00, 0x27, 0x10, 0x00, 0x00, 0x05, 0x97, 0x00, 0x00, 0x27, 0x10,
        0x00, 0x00, 0x08, 0xb0, 0x00, 0x00, 0x27, 0x10, 0x00, 0x00, 0x1c,
        0x01, 0x00, 0x00, 0x27, 0x10, 0x00, 0x00, 0x02, 0x5e, 0x00, 0x00,
        0x27, 0x10, 0x00, 0x00, 0x00, 0x8b, 0x00, 0x00, 0x27, 0x10, 0x00,
        0x00, 0x03, 0xcb, 0x00, 0x00, 0x27, 0x10, 0x00, 0x00, 0x1b, 0xe5,
        0x00, 0x00, 0x27, 0x10, 0x00, 0x28, 0x82, 0x9a, 0x00, 0x05, 0x00,
        0x00, 0x00, 0x01, 0x00, 0x00, 0x03, 0x6a, 0x82, 0x9d, 0x00, 0x05,
        0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x03, 0x72, 0x88, 0x22, 0x00,
        0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x00, 0x90, 0x00,
        0x00, 0x07, 0x00, 0x00, 0x00, 0x04, 0x30, 0x32, 0x32, 0x30, 0x90,
        0x03, 0x00, 0x02, 0x00, 0x00, 0x00, 0x14, 0x00, 0x00, 0x03, 0x7a,
        0x90, 0x04, 0x00, 0x02, 0x00, 0x00, 0x00, 0x14, 0x00, 0x00, 0x03,
        0x8e, 0x91, 0x01, 0x00, 0x07, 0x00, 0x00, 0x00, 0x04, 0x01, 0x02,
        0x03, 0x00, 0x91, 0x02, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, 0x00,
        0x00, 0x03, 0xa2, 0x92, 0x01, 0x00, 0x0a, 0x00, 0x00, 0x00, 0x01,
        0x00, 0x00, 0x03, 0xaa, 0x92, 0x02, 0x00, 0x05, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x03, 0xb2, 0x92, 0x04, 0x00, 0x0a, 0x00, 0x00,
        0x00, 0x01, 0x00, 0x00, 0x03, 0xba, 0x92, 0x05, 0x00, 0x05, 0x00,
        0x00, 0x00, 0x01, 0x00, 0x00, 0x03, 0xc2, 0x92, 0x07, 0x00, 0x03,
        0x00, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x00, 0x92, 0x08, 0x00,
        0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x92, 0x09,
        0x00, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x92,
        0x0a, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x03, 0xca,
        0x92, 0x7c, 0x00, 0x07, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00,
        0x00, 0x92, 0x86, 0x00, 0x07, 0x00, 0x00, 0x00, 0x16, 0x00, 0x00,
        0x03, 0xd2, 0xa0, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x04, 0x30,
        0x31, 0x30, 0x30, 0xa0, 0x01, 0x00, 0x03, 0x00, 0x00, 0x00, 0x01,
        0x00, 0x01, 0x00, 0x00, 0xa0, 0x02, 0x00, 0x03, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x60, 0x00, 0x00, 0xa0, 0x03, 0x00, 0x03, 0x00, 0x00,
        0x00, 0x01, 0x00, 0x48, 0x00, 0x00, 0xa0, 0x05, 0x00, 0x04, 0x00,
        0x00, 0x00, 0x01, 0x00, 0x00, 0x04, 0x00, 0xa2, 0x0e, 0x00, 0x05,
        0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x03, 0xe8, 0xa2, 0x0f, 0x00,
        0x05, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x03, 0xf0, 0xa2, 0x10,
        0x00, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x00, 0xa2,
        0x17, 0x00, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x00,
        0xa3, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x01, 0x03, 0x00, 0x00,
        0x00, 0xa3, 0x01, 0x00, 0x07, 0x00, 0x00, 0x00, 0x01, 0x01, 0x00,
        0x00, 0x00, 0xa4, 0x01, 0x00, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00,
        0x00, 0x00, 0x00, 0xa4, 0x02, 0x00, 0x03, 0x00, 0x00, 0x00, 0x01,
        0x00, 0x00, 0x00, 0x00, 0xa4, 0x03, 0x00, 0x03, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0xa4, 0x04, 0x00, 0x05, 0x00, 0x00,
        0x00, 0x01, 0x00, 0x00, 0x03, 0xf8, 0xa4, 0x05, 0x00, 0x03, 0x00,
        0x00, 0x00, 0x01, 0x00, 0x1d, 0x00, 0x00, 0xa4, 0x06, 0x00, 0x03,
        0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0xa4, 0x07, 0x00,
        0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0xa4, 0x08,
        0x00, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0xa4,
        0x09, 0x00, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00,
        0xa4, 0x0a, 0x00, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00,
        0x00, 0xa4, 0x0c, 0x00, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x02,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x53, 0x00,
        0x00, 0x27, 0x10, 0x00, 0x00, 0x01, 0x5e, 0x00, 0x00, 0x00, 0x64,
        0x32, 0x30, 0x30, 0x38, 0x3a, 0x31, 0x30, 0x3a, 0x32, 0x39, 0x20,
        0x31, 0x33, 0x3a, 0x35, 0x35, 0x3a, 0x33, 0x31, 0x00, 0x32, 0x30,
        0x30, 0x38, 0x3a, 0x31, 0x30, 0x3a, 0x32, 0x39, 0x20, 0x31, 0x33,
        0x3a, 0x35, 0x35, 0x3a, 0x34, 0x37, 0x00, 0x00, 0x00, 0x29, 0x88,
        0x00, 0x00, 0x1b, 0x00, 0x00, 0x00, 0x02, 0xb2, 0x00, 0x00, 0x00,
        0x64, 0x00, 0x00, 0x01, 0x5e, 0x00, 0x00, 0x00, 0x64, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x64, 0x00, 0x00, 0x00, 0x25, 0x00,
        0x00, 0x00, 0x0a, 0x00, 0x00, 0x0e, 0x92, 0x00, 0x00, 0x03, 0xe8,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x32, 0x30, 0x30,
        0x38, 0x31, 0x30, 0x32, 0x39, 0x31, 0x33, 0x35, 0x35, 0x33, 0x31,
        0x00, 0x00, 0x20, 0x2a, 0x00, 0x00, 0x00, 0x0a, 0x00, 0x00, 0x2a,
        0xe2, 0x00, 0x00, 0x00, 0x0a, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x01, 0x00, 0x02, 0x00, 0x01, 0x00, 0x02, 0x00, 0x00, 0x00,
        0x04, 0x52, 0x39, 0x38, 0x00, 0x00, 0x02, 0x00, 0x07, 0x00, 0x00,
        0x00, 0x04, 0x30, 0x31, 0x30, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x06, 0x01, 0x03, 0x00, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x06,
        0x00, 0x00, 0x01, 0x1a, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, 0x00,
        0x00, 0x04, 0x6c, 0x01, 0x1b, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01,
        0x00, 0x00, 0x04, 0x74, 0x01, 0x28, 0x00, 0x03, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x02, 0x00, 0x00, 0x02, 0x01, 0x00, 0x04, 0x00, 0x00,
        0x00, 0x01, 0x00, 0x00, 0x04, 0x7c, 0x02, 0x02, 0x00, 0x04, 0x00,
        0x00, 0x00, 0x01, 0x00, 0x00, 0x05, 0x8b, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00,
        0x48, 0x00, 0x00, 0x00, 0x01, 0xff, 0xd8, 0xff, 0xdb, 0x00, 0x84,
        0x00, 0x20, 0x16, 0x18, 0x1c, 0x18, 0x14, 0x20, 0x1c, 0x1a, 0x1c,
        0x24, 0x22, 0x20, 0x26, 0x30, 0x50, 0x34, 0x30, 0x2c, 0x2c, 0x30,
        0x62, 0x46, 0x4a, 0x3a, 0x50, 0x74, 0x66, 0x7a, 0x78, 0x72, 0x66,
        0x70, 0x6e, 0x80, 0x90, 0xb8, 0x9c, 0x80, 0x88, 0xae, 0x8a, 0x6e,
        0x70, 0xa0, 0xda, 0xa2, 0xae, 0xbe, 0xc4, 0xce, 0xd0, 0xce, 0x7c,
        0x9a, 0xe2, 0xf2, 0xe0, 0xc8, 0xf0, 0xb8, 0xca, 0xce, 0xc6, 0x01,
        0x22, 0x24, 0x24, 0x30, 0x2a, 0x30, 0x5e, 0x34, 0x34, 0x5e, 0xc6,
        0x84, 0x70, 0x84, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6,
        0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6,
        0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6,
        0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6,
        0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xc6, 0xff, 0xc0,
        0x00, 0x11, 0x08, 0x00, 0x78, 0x00, 0xa0, 0x03, 0x01, 0x21, 0x00,
        0x02, 0x11, 0x01, 0x03, 0x11, 0x01, 0xff, 0xc4, 0x01, 0xa2, 0x00,
        0x00, 0x01, 0x05, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
        0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x10, 0x00, 0x02, 0x01, 0x03,
        0x03, 0x02, 0x04, 0x03, 0x05, 0x05, 0x04, 0x04, 0x00, 0x00, 0x01,
        0x7d, 0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31,
        0x41, 0x06, 0x13, 0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81,
        0x91, 0xa1, 0x08, 0x23, 0x42, 0xb1, 0xc1, 0x15, 0x52, 0xd1, 0xf0,
        0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0a, 0x16, 0x17, 0x18, 0x19,
        0x1a, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x34, 0x35, 0x36, 0x37,
        0x38, 0x39, 0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a,
        0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64, 0x65,
        0x66, 0x67, 0x68, 0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78,
        0x79, 0x7a, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x92,
        0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4,
        0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6,
        0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8,
        0xc9, 0xca, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda,
        0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf1,
        0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa, 0x01, 0x00,
        0x03, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
        0x07, 0x08, 0x09, 0x0a, 0x0b, 0x11, 0x00, 0x02, 0x01, 0x02, 0x04,
        0x04, 0x03, 0x04, 0x07, 0x05, 0x04, 0x04, 0x00, 0x01, 0x02, 0x77,
        0x00, 0x01, 0x02, 0x03, 0x11, 0x04, 0x05, 0x21, 0x31, 0x06, 0x12,
        0x41, 0x51, 0x07, 0x61, 0x71, 0x13, 0x22, 0x32, 0x81, 0x08, 0x14,
        0x42, 0x91, 0xa1, 0xb1, 0xc1, 0x09, 0x23, 0x33, 0x52, 0xf0, 0x15,
        0x62, 0x72, 0xd1, 0x0a, 0x16, 0x24, 0x34, 0xe1, 0x25, 0xf1, 0x17,
        0x18, 0x19, 0x1a, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x35, 0x36, 0x37,
        0x38, 0x39, 0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a,
        0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64, 0x65,
        0x66, 0x67, 0x68, 0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78,
        0x79, 0x7a, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a,
        0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3,
        0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5,
        0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7,
        0xc8, 0xc9, 0xca, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9,
        0xda, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf2,
        0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa, 0xff, 0xda, 0x00,
        0x0c, 0x03, 0x01, 0x00, 0x02, 0x11, 0x03, 0x11, 0x00, 0x3f, 0x00,
        0x14, 0x54, 0xaa, 0x2a, 0x46, 0x48, 0xa2, 0xa4, 0x55, 0xa6, 0x04,
        0x8a, 0x29, 0xe0, 0x53, 0x10, 0xe0, 0x29, 0xc0, 0x50, 0x03, 0xb1,
        0x46, 0x29, 0x80, 0x84, 0x52, 0x11, 0x40, 0x0d, 0x22, 0x9a, 0x45,
        0x20, 0x23, 0x61, 0x51, 0x30, 0xa0, 0x08, 0xc8, 0xa8, 0xd8, 0x52,
        0x02, 0x26, 0x15, 0x0b, 0x0a, 0x00, 0xb4, 0xa2, 0xa5, 0x5a, 0x00,
        0x91, 0x45, 0x4a, 0xa2, 0x81, 0x92, 0x01, 0x4e, 0x02, 0x98, 0x87,
        0x0a, 0x70, 0xa0, 0x07, 0x62, 0x8c, 0x50, 0x21, 0x0d, 0x25, 0x00,
        0x34, 0x8a, 0x61, 0x14, 0x0c, 0x63, 0x0a, 0x89, 0x85, 0x00, 0x46,
        0xd5, 0x1b, 0x52, 0x02, 0x16, 0xa8, 0x98, 0x50, 0x05, 0x94, 0xa9,
        0x16, 0x80, 0x25, 0x5a, 0x95, 0x68, 0x18, 0xf1, 0x4f, 0x14, 0xc4,
        0x3b, 0xb5, 0x22, 0xb6, 0x38, 0x34, 0x00, 0xe3, 0x22, 0x8e, 0xf4,
        0x79, 0x8a, 0x7b, 0xd1, 0x71, 0x03, 0x30, 0xc7, 0x14, 0x83, 0xa5,
        0x00, 0x06, 0x98, 0x68, 0x01, 0x8d, 0x51, 0x35, 0x03, 0x22, 0x6a,
        0x8d, 0xa9, 0x01, 0x13, 0x54, 0x4d, 0x40, 0x13, 0xa5, 0x4a, 0x28,
        0x02, 0x45, 0x35, 0x2a, 0x9a, 0x00, 0x78, 0x34, 0xf0, 0x69, 0x80,
        0x34, 0x81, 0x45, 0x40, 0xce, 0x58, 0xe6, 0xa2, 0x4c, 0x06, 0xe4,
        0xfa, 0xd1, 0x93, 0x50, 0x21, 0xca, 0xe4, 0x55, 0x84, 0x90, 0x30,
        0xab, 0x8b, 0x18, 0xa6, 0x9a, 0x6a, 0xc4, 0x31, 0xaa, 0x26, 0xa0,
        0x64, 0x4d, 0x51, 0xb5, 0x20, 0x23, 0x6a, 0x89, 0xa8, 0x02, 0x44,
        0x35, 0x2a, 0x9a, 0x00, 0x95, 0x4d, 0x48, 0xa6, 0x80, 0x24, 0x53,
        0x4e, 0xce, 0x05, 0x30, 0x2b, 0x3b, 0xee, 0x6a, 0x91, 0x5d, 0x76,
        0x63, 0xbd, 0x65, 0x7d, 0x40, 0x66, 0x68, 0xa9, 0x02, 0x45, 0x2b,
        0xb3, 0x9e, 0xb4, 0xc5, 0x6d, 0xad, 0x9a, 0xa0, 0x2c, 0x06, 0xc8,
        0xcd, 0x04, 0xd6, 0xa2, 0x23, 0x63, 0x51, 0xb1, 0xa0, 0x64, 0x4d,
        0x51, 0x93, 0x48, 0x08, 0xda, 0xa2, 0x6a, 0x00, 0x72, 0x1a, 0x99,
        0x4d, 0x00, 0x48, 0xa6, 0xa4, 0x53, 0x4c, 0x07, 0x86, 0x03, 0xbd,
        0x2b, 0x9c, 0xa7, 0x14, 0x98, 0x10, 0x85, 0x34, 0xe0, 0xa6, 0xb3,
        0xb0, 0x0b, 0xb5, 0xa8, 0x0a, 0xd4, 0x58, 0x42, 0xed, 0x3e, 0x94,
        0xd2, 0xa6, 0x8b, 0x01, 0x34, 0x44, 0xed, 0xe6, 0x9c, 0x4d, 0x6a,
        0x80, 0x8d, 0x8d, 0x46, 0xc6, 0x80, 0x23, 0x63, 0x51, 0x9a, 0x06,
        0x46, 0xd5, 0x13, 0x52, 0x01, 0x54, 0xd4, 0xaa, 0x68, 0x02, 0x40,
        0x6a, 0x40, 0x78, 0xa0, 0x08, 0x59, 0xce, 0xee, 0xb5, 0x2a, 0x39,
        0xd9, 0x59, 0xa7, 0xa8, 0x00, 0x73, 0xeb, 0x4e, 0x0e, 0x7d, 0x69,
        0x5c, 0x05, 0xf3, 0x0f, 0xad, 0x1e, 0x61, 0xf5, 0xa7, 0x71, 0x0b,
        0xe6, 0x35, 0x21, 0x90, 0xd3, 0xb8, 0x0e, 0x32, 0x10, 0x95, 0x10,
        0x91, 0xb3, 0xd6, 0x9b, 0x60, 0x4b, 0x9c, 0x8a, 0x63, 0x1a, 0xb0,
        0x18, 0x4d, 0x46, 0xc6, 0x80, 0x22, 0x6a, 0x61, 0xa4, 0x31, 0xaa,
        0x6a, 0x55, 0x34, 0x01, 0x2a, 0x9a, 0x7e, 0x78, 0xa0, 0x08, 0x09,
        0xf9, 0xaa, 0x58, 0xcf, 0xca, 0x6b, 0x3e, 0xa0, 0x00, 0xd3, 0x81,
        0xa9, 0x01, 0x73, 0x46, 0x69, 0x80, 0xb9, 0xa4, 0xcd, 0x00, 0x2b,
        0x1f, 0x92, 0xa3, 0x07, 0x9a, 0x6f, 0x70, 0x26, 0xcf, 0x14, 0xd2,
        0x6b, 0x51, 0x0c, 0x63, 0x51, 0xb1, 0xa0, 0x08, 0xda, 0x98, 0x69,
        0x0c, 0x8d, 0x4d, 0x4a, 0xa6, 0x80, 0x24, 0x53, 0x52, 0x03, 0xc5,
        0x02, 0x21, 0x27, 0xe6, 0xa9, 0x23, 0x3f, 0x29, 0xac, 0xfa, 0x8c,
        0x01, 0xe6, 0x9c, 0x0d, 0x48, 0x0a, 0x0d, 0x2e, 0x68, 0x01, 0x73,
        0x49, 0x9a, 0x60, 0x2b, 0x1f, 0x92, 0x98, 0x3a, 0xd3, 0x7b, 0x81,
        0x36, 0x78, 0xa6, 0x93, 0x5a, 0x88, 0x8c, 0x9a, 0x63, 0x1a, 0x00,
        0x8c, 0xd3, 0x0d, 0x21, 0x91, 0x29, 0xa9, 0x14, 0xd0, 0x04, 0x8a,
        0x69, 0xe0, 0xd3, 0x11, 0x1b, 0x1e, 0x6a, 0x48, 0xcf, 0xca, 0x6b,
        0x3e, 0xa3, 0x10, 0x1a, 0x70, 0x35, 0x20, 0x38, 0x1a, 0x5c, 0xd2,
        0x01, 0x73, 0x49, 0x9a, 0x60, 0x39, 0x8f, 0xca, 0x29, 0x8b, 0xf7,
        0xaa, 0xba, 0x88, 0x96, 0x9a, 0x6b, 0x40, 0x18, 0xc6, 0xa3, 0x26,
        0x80, 0x18, 0x69, 0xa6, 0x90, 0xc8, 0x14, 0xd4, 0x8a, 0x69, 0x80,
        0xf0, 0x6a, 0x40, 0x68, 0x10, 0xbb, 0x41, 0xa7, 0xe3, 0x0b, 0xc5,
        0x2b, 0x01, 0x10, 0xa7, 0x03, 0x59, 0x0c, 0x76, 0x69, 0x73, 0x40,
        0x0b, 0x9a, 0x28, 0x11, 0x28, 0x19, 0x5e, 0x69, 0x02, 0x81, 0x5a,
        0xd8, 0x00, 0xd3, 0x4d, 0x50, 0x0c, 0x6a, 0x8c, 0xd2, 0x01, 0xa6,
        0x98, 0x69, 0x0c, 0xae, 0xa6, 0xa4, 0x06, 0x80, 0x1e, 0xa6, 0x9e,
        0x0d, 0x31, 0x12, 0x03, 0x4f, 0x06, 0x80, 0x13, 0x60, 0x34, 0xd3,
        0xc1, 0xa8, 0x92, 0x01, 0xf1, 0x8d, 0xdd, 0x69, 0xcc, 0xa1, 0x69,
        0x5b, 0x4b, 0x80, 0x83, 0x93, 0x52, 0x04, 0x14, 0xe2, 0xae, 0x03,
        0xa9, 0x0d, 0x68, 0x03, 0x4d, 0x34, 0xd0, 0x03, 0x0d, 0x30, 0xd2,
        0x01, 0x86, 0x9a, 0x68, 0x19, 0x58, 0x1a, 0x78, 0xa4, 0x04, 0x8a,
        0x69, 0xe0, 0xd3, 0x10, 0xe0, 0x69, 0xe0, 0xd0, 0x03, 0xc1, 0xa8,
        0xdb, 0xad, 0x4c, 0x81, 0x12, 0x45, 0xd6, 0x9d, 0x25, 0x1d, 0x00,
        0x6a, 0xf5, 0xa9, 0xe8, 0x80, 0x31, 0x29, 0x0d, 0x58, 0x08, 0x69,
        0x86, 0x80, 0x1a, 0x69, 0x86, 0x90, 0x0c, 0x34, 0xd3, 0x48, 0x65,
        0x51, 0x4f, 0x06, 0x98, 0x0f, 0x14, 0xf0, 0x68, 0x10, 0xf0, 0x69,
        0xe0, 0xd0, 0x03, 0x81, 0xa5, 0x2b, 0x9a, 0x1a, 0xb8, 0x87, 0xa8,
        0xdb, 0x4a, 0x46, 0x68, 0xb6, 0x80, 0x2a, 0xa8, 0x14, 0xea, 0x12,
        0xb0, 0x05, 0x21, 0xa6, 0x02, 0x1a, 0x61, 0xa0, 0x06, 0x9a, 0x61,
        0xa4, 0x31, 0x86, 0x9a, 0x69, 0x0c, 0xa8, 0x0d, 0x3c, 0x53, 0x01,
        0xe2, 0x9e, 0x28, 0x10, 0xf1, 0x4e, 0x06, 0x98, 0x0f, 0x06, 0x9e,
        0x0d, 0x02, 0x1c, 0x29, 0xc2, 0x80, 0x16, 0x96, 0x80, 0x0a, 0x4a,
        0x00, 0x43, 0x4d, 0x34, 0x0c, 0x61, 0xa6, 0x1a, 0x40, 0x34, 0xd3,
        0x4d, 0x21, 0x80, 0xff, 0xd9, 0xff, 0xdb, 0x00, 0x84, 0x00, 0x0a,
        0x07, 0x07, 0x08, 0x07, 0x06, 0x0a, 0x08, 0x08, 0x08, 0x0b, 0x0a,
        0x0a, 0x0b, 0x0e, 0x18, 0x10, 0x0e, 0x0d, 0x0d, 0x0e, 0x1d, 0x15,
        0x16, 0x11, 0x18, 0x23, 0x1f, 0x25, 0x24, 0x22, 0x1f, 0x22, 0x21,
        0x26, 0x2b, 0x37, 0x2f, 0x26, 0x29, 0x34, 0x29, 0x21, 0x22, 0x30,
        0x41, 0x31, 0x34, 0x39, 0x3b, 0x3e, 0x3e, 0x3e, 0x25, 0x2e, 0x44,
        0x49, 0x43, 0x3c, 0x48, 0x37, 0x3d, 0x3e, 0x3b, 0x01, 0x0a, 0x0b,
        0x0b, 0x0e, 0x0d, 0x0e, 0x1c, 0x10, 0x10, 0x1c, 0x3b, 0x28, 0x22,
        0x28, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b,
        0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b,
        0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b,
        0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b,
        0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0x3b, 0xff, 0xc0, 0x00, 0x11,
        0x08, 0x00, 0x48, 0x00, 0x60, 0x03, 0x01, 0x21, 0x00, 0x02, 0x11,
        0x01, 0x03, 0x11, 0x01, 0xff, 0xc4, 0x01, 0xa2, 0x00, 0x00, 0x01,
        0x05, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x10, 0x00, 0x02, 0x01, 0x03, 0x03, 0x02,
        0x04, 0x03, 0x05, 0x05, 0x04, 0x04, 0x00, 0x00, 0x01, 0x7d, 0x01,
        0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06,
        0x13, 0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xa1,
        0x08, 0x23, 0x42, 0xb1, 0xc1, 0x15, 0x52, 0xd1, 0xf0, 0x24, 0x33,
        0x62, 0x72, 0x82, 0x09, 0x0a, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x25,
        0x26, 0x27, 0x28, 0x29, 0x2a, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
        0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x53, 0x54,
        0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67,
        0x68, 0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a,
        0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x92, 0x93, 0x94,
        0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6,
        0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8,
        0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca,
        0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe1, 0xe2,
        0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf1, 0xf2, 0xf3,
        0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa, 0x01, 0x00, 0x03, 0x01,
        0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
        0x09, 0x0a, 0x0b, 0x11, 0x00, 0x02, 0x01, 0x02, 0x04, 0x04, 0x03,
        0x04, 0x07, 0x05, 0x04, 0x04, 0x00, 0x01, 0x02, 0x77, 0x00, 0x01,
        0x02, 0x03, 0x11, 0x04, 0x05, 0x21, 0x31, 0x06, 0x12, 0x41, 0x51,
        0x07, 0x61, 0x71, 0x13, 0x22, 0x32, 0x81, 0x08, 0x14, 0x42, 0x91,
        0xa1, 0xb1, 0xc1, 0x09, 0x23, 0x33, 0x52, 0xf0, 0x15, 0x62, 0x72,
        0xd1, 0x0a, 0x16, 0x24, 0x34, 0xe1, 0x25, 0xf1, 0x17, 0x18, 0x19,
        0x1a, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x35, 0x36, 0x37, 0x38, 0x39,
        0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x53, 0x54,
        0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67,
        0x68, 0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a,
        0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x92, 0x93,
        0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5,
        0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7,
        0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9,
        0xca, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe2,
        0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf2, 0xf3, 0xf4,
        0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa, 0xff, 0xda, 0x00, 0x0c, 0x03,
        0x01, 0x00, 0x02, 0x11, 0x03, 0x11, 0x00, 0x3f, 0x00, 0x9e, 0xd2,
        0x2e, 0x07, 0x15, 0xaf, 0x6d, 0x08, 0xe2, 0xb3, 0x45, 0x1a, 0xf6,
        0xd0, 0x00, 0x01, 0xc5, 0x68, 0x45, 0x17, 0x4a, 0xb4, 0x22, 0xe4,
        0x70, 0x8c, 0x74, 0xa9, 0x3c, 0xa1, 0x8e, 0x95, 0x48, 0x96, 0x31,
        0xe2, 0x18, 0xe9, 0x55, 0xa5, 0x8c, 0x7a, 0x50, 0x05, 0x0b, 0x88,
        0x86, 0x0f, 0x15, 0x8f, 0x75, 0x1f, 0x26, 0x93, 0x19, 0x91, 0x77,
        0x18, 0xc1, 0xac, 0x4b, 0xc8, 0xfa, 0xd6, 0x63, 0x37, 0x6d, 0x31,
        0xb4, 0x73, 0x5b, 0x36, 0xa0, 0x1c, 0x50, 0x80, 0xd7, 0x83, 0xa0,
        0xab, 0xd1, 0x62, 0xad, 0x09, 0x8f, 0x17, 0x29, 0x03, 0xb2, 0xcc,
        0xe0, 0x77, 0x14, 0xa3, 0x56, 0xb3, 0x27, 0x1e, 0x67, 0xe9, 0x52,
        0xea, 0xc6, 0x3a, 0x36, 0x48, 0xef, 0x3d, 0x27, 0x70, 0x22, 0x60,
        0x47, 0x52, 0x69, 0xb2, 0xe2, 0xad, 0x3b, 0xea, 0x80, 0xa3, 0x38,
        0xe0, 0xd6, 0x3d, 0xd8, 0x1c, 0xd0, 0xca, 0x46, 0x3d, 0xd0, 0x18,
        0x35, 0x89, 0x78, 0xa3, 0x9a, 0xcd, 0x8c, 0xd2, 0xb3, 0x93, 0x2a,
        0x2b, 0x66, 0xd5, 0xf1, 0x8a, 0x10, 0x1a, 0xd6, 0xf2, 0x03, 0x8a,
        0x9e, 0xe6, 0xf4, 0x5a, 0xdb, 0xef, 0xfe, 0x23, 0xc0, 0xa7, 0x27,
        0xcb, 0x16, 0xc4, 0xcc, 0xdd, 0xe2, 0x78, 0x9a, 0x69, 0x66, 0xcc,
        0x99, 0xe1, 0x4d, 0x47, 0xba, 0xbc, 0xd9, 0x6a, 0xee, 0x26, 0x59,
        0x59, 0x4d, 0xac, 0x69, 0x34, 0x52, 0xe5, 0x8f, 0x55, 0xad, 0x58,
        0xae, 0x85, 0xc4, 0x22, 0x41, 0xdf, 0xad, 0x76, 0x61, 0xe5, 0x6f,
        0x74, 0x45, 0x69, 0xdc, 0x00, 0x79, 0xac, 0x8b, 0xa6, 0xc9, 0x35,
        0xd4, 0x34, 0x64, 0xdc, 0x37, 0x06, 0xb1, 0xae, 0x88, 0xc1, 0xac,
        0xd8, 0xc9, 0x2c, 0xa6, 0xe0, 0x73, 0x5b, 0x36, 0xf3, 0x74, 0xe6,
        0x84, 0x05, 0xe3, 0xa9, 0x47, 0x6a, 0x14, 0xb6, 0x49, 0x3d, 0x85,
        0x3a, 0xee, 0xee, 0x2b, 0xa8, 0xe2, 0x6f, 0x30, 0x81, 0xe9, 0x8a,
        0xca, 0xa4, 0xe2, 0xd3, 0x8b, 0x01, 0xb1, 0xf9, 0x04, 0x7f, 0xaf,
        0x23, 0xf0, 0xa9, 0x54, 0x41, 0x9c, 0xfd, 0xa3, 0xf4, 0xae, 0x65,
        0x18, 0xf7, 0x25, 0x8a, 0xe2, 0x02, 0x38, 0xb8, 0xfd, 0x2a, 0x7b,
        0x5b, 0xa8, 0x6d, 0x6d, 0x5d, 0x9a, 0x5d, 0xcb, 0xbb, 0xd2, 0xb6,
        0xa6, 0xa3, 0x19, 0x5e, 0xe2, 0x03, 0x7b, 0x1d, 0xc2, 0x17, 0x8d,
        0xb8, 0xac, 0xfb, 0x89, 0x39, 0x35, 0xd6, 0x9a, 0x6a, 0xe8, 0x66,
        0x55, 0xcb, 0xf5, 0xac, 0x7b, 0x96, 0xeb, 0x50, 0xc6, 0x88, 0x6d,
        0x66, 0xe9, 0xcd, 0x6c, 0xdb, 0x4f, 0xd3, 0x9a, 0x00, 0x2f, 0xe6,
        0xf9, 0xa3, 0xe7, 0xb5, 0x4a, 0x93, 0x7f, 0xa2, 0xc6, 0x73, 0xdc,
        0xd7, 0x15, 0x55, 0xef, 0x48, 0x7d, 0x09, 0x52, 0x6e, 0x3a, 0xd4,
        0xab, 0x2f, 0xbd, 0x61, 0x16, 0x0c, 0x73, 0x49, 0xc5, 0x24, 0x92,
        0x7f, 0xa2, 0x63, 0xfd, 0xaa, 0xd6, 0x2f, 0x71, 0x0e, 0xb1, 0x93,
        0xf7, 0x2d, 0xf5, 0xa4, 0x9e, 0x4e, 0xb5, 0xdd, 0x4b, 0xf8, 0x68,
        0x4c, 0xcb, 0xb9, 0x93, 0xad, 0x65, 0xce, 0xd9, 0x26, 0xa9, 0x8d,
        0x19, 0xf6, 0xf2, 0xf4, 0xe6, 0xb5, 0xad, 0xe7, 0xc6, 0x39, 0xa0,
        0x18, 0xeb, 0xc9, 0x77, 0x6c, 0x35, 0x2a, 0x4b, 0xfe, 0x8a, 0x9c,
        0xff, 0x00, 0x11, 0xae, 0x3a, 0x8b, 0xde, 0x61, 0xd0, 0x9e, 0x39,
        0xb8, 0xeb, 0x53, 0xac, 0xb9, 0xae, 0x5b, 0x00, 0xf3, 0x27, 0x14,
        0x92, 0xc9, 0xfe, 0x8a, 0x3f, 0xde, 0x35, 0xac, 0x3a, 0x88, 0x92,
        0xcd, 0xb1, 0x6e, 0x7d, 0xcd, 0x32, 0x67, 0xeb, 0xcd, 0x7a, 0x14,
        0xfe, 0x04, 0x26, 0x66, 0xce, 0xf9, 0x26, 0xb3, 0xe6, 0x6e, 0xb4,
        0xd9, 0x48, 0xc8, 0x82, 0x4e, 0x07, 0x35, 0xa7, 0x6f, 0x2f, 0x02,
        0x9a, 0x06, 0x5f, 0x8c, 0xa4, 0x83, 0x0e, 0x32, 0x2a, 0x69, 0xe3,
        0xdd, 0x12, 0x08, 0x97, 0x85, 0xec, 0x2a, 0x2a, 0x42, 0xf1, 0x76,
        0x26, 0xe4, 0x6a, 0x59, 0x0e, 0x18, 0x10, 0x6a, 0xd2, 0x89, 0x02,
        0x6e, 0x2a, 0x71, 0xeb, 0x5c, 0x1c, 0x8c, 0xa6, 0x48, 0xbb, 0xdc,
        0x61, 0x41, 0x35, 0x72, 0x28, 0x87, 0xd9, 0xf6, 0x4a, 0xb9, 0xe7,
        0x38, 0xae, 0x8c, 0x3d, 0x36, 0xdd, 0xde, 0xc4, 0xb0, 0x21, 0x51,
        0x76, 0xa8, 0xc0, 0xaa, 0x93, 0x31, 0xe6, 0xbb, 0x2d, 0x65, 0x61,
        0x19, 0xd3, 0x1e, 0xb5, 0x46, 0x5a, 0x96, 0x5a, 0x30, 0xa0, 0x7e,
        0x05, 0x69, 0x5b, 0xc9, 0xc6, 0x28, 0x40, 0xcd, 0x08, 0x64, 0x3c,
        0x73, 0x57, 0xe1, 0x94, 0xf1, 0xcd, 0x5a, 0x21, 0x8c, 0xb9, 0x63,
        0xe7, 0x67, 0x1d, 0xab, 0x40, 0xb1, 0xfb, 0x00, 0x1d, 0xf0, 0x2b,
        0x99, 0x2d, 0x66, 0x3e, 0x88, 0x75, 0x81, 0x3f, 0x31, 0xf6, 0xab,
        0x64, 0xd6, 0xb4, 0x17, 0xee, 0xd0, 0x9e, 0xe4, 0x32, 0x1a, 0xa7,
        0x31, 0xad, 0x18, 0x14, 0x26, 0xef, 0x54, 0xa5, 0xa8, 0x65, 0xa3,
        0x9c, 0x81, 0xfa, 0x56, 0x8c, 0x2d, 0xce, 0x68, 0x40, 0xcb, 0xf1,
        0x37, 0xbd, 0x5e, 0x85, 0xea, 0xd1, 0x0c, 0xbb, 0x19, 0x56, 0x23,
        0x20, 0x1f, 0xad, 0x5c, 0x42, 0x08, 0x03, 0xb5, 0x55, 0x91, 0x04,
        0xc9, 0x80, 0x38, 0x00, 0x0a, 0x71, 0x34, 0x6c, 0x32, 0x27, 0xe9,
        0x55, 0x25, 0x15, 0x2c, 0x68, 0xa3, 0x30, 0xeb, 0x54, 0xa5, 0x15,
        0x0c, 0xd1, 0x00, 0xff, 0xd9};

    /* package */ static final byte[] sPhotoByteArrayForComplicatedCase;

    static {
        final int length = sPhotoIntArrayForComplicatedCase.length;
        sPhotoByteArrayForComplicatedCase = new byte[length];
        for (int i = 0; i < length; i++) {
            sPhotoByteArrayForComplicatedCase[i] = (byte)sPhotoIntArrayForComplicatedCase[i];
        }
    }

    public void testV21SimpleCase1_Parsing() {
        mVerifier.initForImportTest(V21, R.raw.v21_simple_1);
        mVerifier.addPropertyNodesVerifierElemWithoutVersion()  // no "VERSION:2.1" line.
                .addExpectedNodeWithOrder("N", "Ando;Roid;", Arrays.asList("Ando", "Roid", ""));
    }

    public void testV21SimpleCase1_Type_Generic() {
        mVerifier.initForImportTest(VCardConfig.VCARD_TYPE_V21_GENERIC, R.raw.v21_simple_1);
        mVerifier.addContentValuesVerifierElem()
                .addExpected(StructuredName.CONTENT_ITEM_TYPE)
                        .put(StructuredName.FAMILY_NAME, "Ando")
                        .put(StructuredName.GIVEN_NAME, "Roid")
                        .put(StructuredName.DISPLAY_NAME, "Roid Ando");
    }

    public void testV21SimpleCase1_Type_Japanese() {
        mVerifier.initForImportTest(VCardConfig.VCARD_TYPE_V21_JAPANESE, R.raw.v21_simple_1);
        mVerifier.addContentValuesVerifierElem()
                .addExpected(StructuredName.CONTENT_ITEM_TYPE)
                        .put(StructuredName.FAMILY_NAME, "Ando")
                        .put(StructuredName.GIVEN_NAME, "Roid")
                        // If name-related strings only contains printable Ascii,
                        // the order is remained to be US's:
                        // "Prefix Given Middle Family Suffix"
                        .put(StructuredName.DISPLAY_NAME, "Roid Ando");
    }

    public void testV21SimpleCase2() {
        mVerifier.initForImportTest(VCardConfig.VCARD_TYPE_V21_JAPANESE, R.raw.v21_simple_2);
        mVerifier.addContentValuesVerifierElem()
                .addExpected(StructuredName.CONTENT_ITEM_TYPE)
                        .put(StructuredName.DISPLAY_NAME, "Ando Roid");
    }

    public void testV21SimpleCase3() {
        mVerifier.initForImportTest(V21, R.raw.v21_simple_3);
        mVerifier.addContentValuesVerifierElem()
                .addExpected(StructuredName.CONTENT_ITEM_TYPE)
                        .put(StructuredName.FAMILY_NAME, "Ando")
                        .put(StructuredName.GIVEN_NAME, "Roid")
                        // "FN" field should be prefered since it should contain the original
                        // order intended by the author of the file.
                        .put(StructuredName.DISPLAY_NAME, "Ando Roid");
    }

    /**
     * Tests ';' is properly handled by VCardParser implementation.
     */
    public void testV21BackslashCase_Parsing() {
        mVerifier.initForImportTest(V21, R.raw.v21_backslash);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", ";A;B\\;C\\;;D;:E;\\\\;",
                        Arrays.asList("", "A;B\\", "C\\;", "D", ":E", "\\\\", ""))
                .addExpectedNodeWithOrder("FN", "A;B\\C\\;D:E\\\\");

    }

    /**
     * Tests ContactStruct correctly ignores redundant fields in "N" property values and
     * inserts name related data.
     */
    public void testV21BackslashCase() {
        mVerifier.initForImportTest(V21, R.raw.v21_backslash);
        mVerifier.addContentValuesVerifierElem()
                .addExpected(StructuredName.CONTENT_ITEM_TYPE)
                        // FAMILY_NAME is empty and removed in this test...
                        .put(StructuredName.GIVEN_NAME, "A;B\\")
                        .put(StructuredName.MIDDLE_NAME, "C\\;")
                        .put(StructuredName.PREFIX, "D")
                        .put(StructuredName.SUFFIX, ":E")
                        .put(StructuredName.DISPLAY_NAME, "A;B\\C\\;D:E\\\\");
    }

    public void testOrgBeforTitle() {
        mVerifier.initForImportTest(V21, R.raw.v21_org_before_title);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.DISPLAY_NAME, "Normal Guy");
        elem.addExpected(Organization.CONTENT_ITEM_TYPE)
                .put(Organization.COMPANY, "Company")
                .put(Organization.DEPARTMENT, "Organization Devision Room Sheet No.")
                .put(Organization.TITLE, "Excellent Janitor")
                .put(Organization.TYPE, Organization.TYPE_WORK);
    }

    public void testTitleBeforOrg() {
        mVerifier.initForImportTest(V21, R.raw.v21_title_before_org);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.DISPLAY_NAME, "Nice Guy");
        elem.addExpected(Organization.CONTENT_ITEM_TYPE)
                .put(Organization.COMPANY, "Marverous")
                .put(Organization.DEPARTMENT, "Perfect Great Good Bad Poor")
                .put(Organization.TITLE, "Cool Title")
                .put(Organization.TYPE, Organization.TYPE_WORK);
    }

    /**
     * Verifies that vCard importer correctly interpret "PREF" attribute to IS_PRIMARY.
     * The data contain three cases: one "PREF", no "PREF" and multiple "PREF", in each type.
     */
    public void testV21PrefToIsPrimary() {
        mVerifier.initForImportTest(V21, R.raw.v21_pref_handling);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.DISPLAY_NAME, "Smith");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.NUMBER, "1")
                .put(Phone.TYPE, Phone.TYPE_HOME);
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.NUMBER, "2")
                .put(Phone.TYPE, Phone.TYPE_WORK)
                .put(Phone.IS_PRIMARY, 1);
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.NUMBER, "3")
                .put(Phone.TYPE, Phone.TYPE_ISDN);
        elem.addExpected(Email.CONTENT_ITEM_TYPE)
                .put(Email.DATA, "test@example.com")
                .put(Email.TYPE, Email.TYPE_HOME)
                .put(Email.IS_PRIMARY, 1);
        elem.addExpected(Email.CONTENT_ITEM_TYPE)
                .put(Email.DATA, "test2@examination.com")
                .put(Email.TYPE, Email.TYPE_MOBILE)
                .put(Email.IS_PRIMARY, 1);
        elem.addExpected(Organization.CONTENT_ITEM_TYPE)
                .put(Organization.COMPANY, "Company")
                .put(Organization.TITLE, "Engineer")
                .put(Organization.TYPE, Organization.TYPE_WORK);
        elem.addExpected(Organization.CONTENT_ITEM_TYPE)
                .put(Organization.COMPANY, "Mystery")
                .put(Organization.TITLE, "Blogger")
                .put(Organization.TYPE, Organization.TYPE_WORK);
        elem.addExpected(Organization.CONTENT_ITEM_TYPE)
                .put(Organization.COMPANY, "Poetry")
                .put(Organization.TITLE, "Poet")
                .put(Organization.TYPE, Organization.TYPE_WORK);
    }

    /**
     * Tests all the properties in a complicated vCard are correctly parsed by the VCardParser for
     * both v2.1 and v3.0 cards.
     */
    public void testV21ComplicatedCase_Parsing() {
        testComplicatedCase_Parsing(V21, R.raw.v21_complicated);
    }

    public void testV30adr_types_various() {
        mVerifier.initForImportTest(V30, R.raw.v30_adr_types);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "Familyname")
                .put(StructuredName.GIVEN_NAME, "Givenname")
                .put(StructuredName.DISPLAY_NAME, "Givenname Familyname");
        elem.addExpected(StructuredPostal.CONTENT_ITEM_TYPE)
                .put(StructuredPostal.TYPE, StructuredPostal.TYPE_WORK)
                .put(StructuredPostal.STREET,
                        "1010 Technology Pkwy, Silicon City, Somecountry")
                .put(StructuredPostal.FORMATTED_ADDRESS,
                        "1010 Technology Pkwy, Silicon City, Somecountry");
        elem.addExpected(StructuredPostal.CONTENT_ITEM_TYPE)
                .put(StructuredPostal.TYPE, StructuredPostal.TYPE_OTHER)
                .put(StructuredPostal.STREET,
                        "123 Main St, Anytown, Anywhere")
                .put(StructuredPostal.FORMATTED_ADDRESS,
                        "123 Main St, Anytown, Anywhere");
        elem.addExpected(StructuredPostal.CONTENT_ITEM_TYPE)
                .put(StructuredPostal.TYPE, StructuredPostal.TYPE_CUSTOM)
                .put(StructuredPostal.LABEL, "School")
                .put(StructuredPostal.STREET,
                        "112358 Academic Lane, College Town, Someplace Great")
                .put(StructuredPostal.FORMATTED_ADDRESS,
                        "112358 Academic Lane, College Town, Someplace Great");
    }

    public void testV30ComplicatedCase_Parsing() {
        testComplicatedCase_Parsing(V30, R.raw.v30_complicated);
    }

    public void testComplicatedCase_Parsing(int vcardType, int resId) {
        mVerifier.initForImportTest(vcardType, resId);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "Gump;Forrest;Hoge;Pos;Tao",
                        Arrays.asList("Gump", "Forrest", "Hoge", "Pos", "Tao"))
                .addExpectedNodeWithOrder("FN", "Joe Due")
                .addExpectedNodeWithOrder("ORG",
                        "Gump Shrimp Co.;Sales Dept.;Manager;Fish keeper",
                        Arrays.asList("Gump Shrimp Co.", "Sales Dept.;Manager", "Fish keeper"))
                .addExpectedNodeWithOrder("ROLE", "Fish Cake Keeper!")
                .addExpectedNodeWithOrder("TITLE", "Shrimp Man")
                .addExpectedNodeWithOrder("X-CLASS", "PUBLIC")
                .addExpectedNodeWithOrder("TEL", "(111) 555-1212", new TypeSet("WORK", "VOICE"))
                .addExpectedNodeWithOrder("TEL", "(404) 555-1212", new TypeSet("HOME", "VOICE"))
                .addExpectedNodeWithOrder("TEL", "(404) 555-1212P*#55", new TypeSet("FAX"))
                .addExpectedNodeWithOrder("TEL", "0311111111", new TypeSet("CELL"))
                .addExpectedNodeWithOrder("TEL", "0322222222", new TypeSet("VIDEO"))
                .addExpectedNodeWithOrder("TEL", "0333333333", new TypeSet("VOICE"))
                .addExpectedNodeWithOrder("ADR",
                        ";;100 Waters Edge;Baytown;LA;30314;United States of America",
                        Arrays.asList("", "", "100 Waters Edge", "Baytown",
                                "LA", "30314", "United States of America"),
                                null, null, new TypeSet("WORK"), null)
                .addExpectedNodeWithOrder("LABEL",
                        "100 Waters Edge\r\nBaytown, LA 30314\r\nUnited  States of America",
                        null, null, mContentValuesForQP, new TypeSet("WORK"), null)
                .addExpectedNodeWithOrder("ADR",
                        ";;42 Plantation St.;Baytown;LA;30314;United States of America",
                        Arrays.asList("", "", "42 Plantation St.", "Baytown",
                                "LA", "30314", "United States of America"), null, null,
                                new TypeSet("HOME"), null)
                .addExpectedNodeWithOrder("LABEL",
                        "42 Plantation St.\r\nBaytown, LA 30314\r\nUnited  States of America",
                        null, null, mContentValuesForQP,
                        new TypeSet("HOME"), null)
                .addExpectedNodeWithOrder("EMAIL", "forrestgump@walladalla.com",
                        new TypeSet("PREF", "INTERNET"))
                .addExpectedNodeWithOrder("EMAIL", "cell@example.com", new TypeSet("CELL"))
                .addExpectedNodeWithOrder("NOTE",
                        "The following note is the example from RFC 2045.")
                .addExpectedNodeWithOrder("NOTE",
                        "Now's the time for all folk to come to the aid of their country.",
                        null, null, mContentValuesForQP, null, null)
                .addExpectedNodeWithOrder("PHOTO", null,
                        null, sPhotoByteArrayForComplicatedCase, mContentValuesForBase64V21,
                        new TypeSet("JPEG"), null)
                .addExpectedNodeWithOrder("X-ATTRIBUTE", "Some String")
                .addExpectedNodeWithOrder("BDAY", "19800101")
                .addExpectedNodeWithOrder("GEO", "35.6563854,139.6994233")
                .addExpectedNodeWithOrder("URL", "http://www.example.com/")
                .addExpectedNodeWithOrder("REV", "20080424T195243Z");
    }

    /**
     * Checks ContactStruct correctly inserts values in a complicated vCard
     * into ContentResolver.
     */
    public void testV21ComplicatedCase() {
        testComplicatedCase(V21, R.raw.v21_complicated);
    }

    public void testV30ComplicatedCase() {
        testComplicatedCase(V30, R.raw.v30_complicated);
    }
    public void testComplicatedCase(int vcardType, int resId) {
        mVerifier.initForImportTest(vcardType, resId);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "Gump")
                .put(StructuredName.GIVEN_NAME, "Forrest")
                .put(StructuredName.MIDDLE_NAME, "Hoge")
                .put(StructuredName.PREFIX, "Pos")
                .put(StructuredName.SUFFIX, "Tao")
                .put(StructuredName.DISPLAY_NAME, "Joe Due");
        elem.addExpected(Organization.CONTENT_ITEM_TYPE)
                .put(Organization.TYPE, Organization.TYPE_WORK)
                .put(Organization.COMPANY, "Gump Shrimp Co.")
                .put(Organization.DEPARTMENT, "Sales Dept.;Manager Fish keeper")
                .put(Organization.TITLE, "Shrimp Man");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_WORK)
                // Phone number is expected to be formated with NAMP format in default.
                .put(Phone.NUMBER, "111-555-1212");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_HOME)
                .put(Phone.NUMBER, "404-555-1212");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_OTHER_FAX)
                .put(Phone.NUMBER, "4045551212,*#55");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_MOBILE)
                .put(Phone.NUMBER, "031-111-1111");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "VIDEO")
                .put(Phone.NUMBER, "032-222-2222");
        // vCard's VOICE type should become OTHER type on Android devices.
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_OTHER)
                .put(Phone.NUMBER, "033-333-3333");
        elem.addExpected(StructuredPostal.CONTENT_ITEM_TYPE)
                .put(StructuredPostal.TYPE, StructuredPostal.TYPE_WORK)
                .put(StructuredPostal.COUNTRY, "United States of America")
                .put(StructuredPostal.POSTCODE, "30314")
                .put(StructuredPostal.REGION, "LA")
                .put(StructuredPostal.CITY, "Baytown")
                .put(StructuredPostal.STREET, "100 Waters Edge")
                .put(StructuredPostal.FORMATTED_ADDRESS,
                        "100 Waters Edge Baytown LA 30314 United States of America");
        elem.addExpected(StructuredPostal.CONTENT_ITEM_TYPE)
                .put(StructuredPostal.TYPE, StructuredPostal.TYPE_HOME)
                .put(StructuredPostal.COUNTRY, "United States of America")
                .put(StructuredPostal.POSTCODE, "30314")
                .put(StructuredPostal.REGION, "LA")
                .put(StructuredPostal.CITY, "Baytown")
                .put(StructuredPostal.STREET, "42 Plantation St.")
                .put(StructuredPostal.FORMATTED_ADDRESS,
                        "42 Plantation St. Baytown LA 30314 United States of America");
        elem.addExpected(Email.CONTENT_ITEM_TYPE)
                // "TYPE=INTERNET" -> TYPE_CUSTOM + the label "INTERNET"
                .put(Email.TYPE, Email.TYPE_CUSTOM)
                .put(Email.LABEL, "INTERNET")
                .put(Email.DATA, "forrestgump@walladalla.com")
                .put(Email.IS_PRIMARY, 1);
        elem.addExpected(Email.CONTENT_ITEM_TYPE)
                .put(Email.TYPE, Email.TYPE_MOBILE)
                .put(Email.DATA, "cell@example.com");
        elem.addExpected(Note.CONTENT_ITEM_TYPE)
                .put(Note.NOTE, "The following note is the example from RFC 2045.");
        elem.addExpected(Note.CONTENT_ITEM_TYPE)
                .put(Note.NOTE,
                        "Now's the time for all folk to come to the aid of their country.");
        elem.addExpected(Photo.CONTENT_ITEM_TYPE)
                // No information about its image format can be inserted.
                .put(Photo.PHOTO, sPhotoByteArrayForComplicatedCase);
        elem.addExpected(Event.CONTENT_ITEM_TYPE)
                .put(Event.START_DATE, "19800101")
                .put(Event.TYPE, Event.TYPE_BIRTHDAY);
        elem.addExpected(Website.CONTENT_ITEM_TYPE)
                .put(Website.URL, "http://www.example.com/")
                .put(Website.TYPE, Website.TYPE_HOMEPAGE);
    }

    public void testInvalidMultipleLineV21() {
        mVerifier.initForImportTest(V21, R.raw.v21_invalid_multiple_line);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.GIVEN_NAME, "Omega")
                .put(StructuredName.DISPLAY_NAME, "Omega");
        elem.addExpected(Email.CONTENT_ITEM_TYPE)
                .put(Email.TYPE, Email.TYPE_CUSTOM)
                .put(Email.LABEL, "INTERNET")
                .put(Email.ADDRESS, "\"Omega\" <omega@example.com>");
    }

    public void testAdrMultipleLineV21() {
        ContentValues contentValuesForValue = new ContentValues();
        contentValuesForValue.put("VALUE", "DATE");

        mVerifier.initForImportTest(V21, R.raw.v21_adr_multiple_line);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "bogus")
                .addExpectedNodeWithOrder("URL", "http://bogus.com/")
                .addExpectedNodeWithOrder("ADR",
                        ";;Grindelberg 999;Hamburg;;99999;Deutschland",
                        Arrays.asList("", "", "Grindelberg 999", "Hamburg", "", "99999",
                                "Deutschland"),
                        new TypeSet("HOME"))
                .addExpectedNodeWithOrder("ADR", ";;Hermann v. Brevern\\ 9999999\\ " +
                        "Packstation 999;Hamburg;;99999;Deutschland",
                        Arrays.asList("", "", "Hermann v. Brevern\\ 9999999\\ Packstation 999",
                                "Hamburg", "", "99999", "Deutschland"),
                        new TypeSet("HOME"))
                .addExpectedNodeWithOrder("BDAY", "20081203", contentValuesForValue);
    }

    public void testV30Simple_Parsing() {
        mVerifier.initForImportTest(V30, R.raw.v30_simple);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("FN", "And Roid")
                .addExpectedNodeWithOrder("N", "And;Roid;;;",
                        Arrays.asList("And", "Roid", "", "", ""))
                .addExpectedNodeWithOrder("ORG", "Open;Handset; Alliance",
                        Arrays.asList("Open", "Handset", " Alliance"))
                .addExpectedNodeWithOrder("SORT-STRING", "android")
                .addExpectedNodeWithOrder("TEL", "0300000000", new TypeSet("PREF", "VOICE"))
                .addExpectedNodeWithOrder("CLASS", "PUBLIC")
                .addExpectedNodeWithOrder("X-GNO", "0")
                .addExpectedNodeWithOrder("X-GN", "group0")
                .addExpectedNodeWithOrder("X-REDUCTION", "0")
                .addExpectedNodeWithOrder("REV", "20081031T065854Z");
    }

    public void testV30Simple() {
        mVerifier.initForImportTest(V30, R.raw.v30_simple);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "And")
                .put(StructuredName.GIVEN_NAME, "Roid")
                .put(StructuredName.DISPLAY_NAME, "And Roid")
                .put(StructuredName.PHONETIC_GIVEN_NAME, "android");
        elem.addExpected(Organization.CONTENT_ITEM_TYPE)
                .put(Organization.COMPANY, "Open")
                .put(Organization.DEPARTMENT, "Handset  Alliance")
                .put(Organization.TYPE, Organization.TYPE_WORK);
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_OTHER)
                .put(Phone.NUMBER, "030-000-0000")
                .put(Phone.IS_PRIMARY, 1);
    }

    public void testV21Japanese1_Parsing() {
        // Though Japanese careers append ";;;;" at the end of the value of "SOUND",
        // vCard 2.1/3.0 specification does not allow multiple values.
        // Do not need to handle it as multiple values.
        mVerifier.initForImportTest(VCardConfig.VCARD_TYPE_V21_JAPANESE, R.raw.v21_japanese_1);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "\u5B89\u85E4\u30ED\u30A4\u30C9;;;;",
                        Arrays.asList("\u5B89\u85E4\u30ED\u30A4\u30C9", "", "", "", ""),
                        null, mContentValuesForSJis, null, null)
                .addExpectedNodeWithOrder("SOUND",
                        "\uFF71\uFF9D\uFF84\uFF9E\uFF73\uFF9B\uFF72\uFF84\uFF9E;;;;",
                        null, null, mContentValuesForSJis,
                        new TypeSet("X-IRMC-N"), null)
                .addExpectedNodeWithOrder("TEL", "0300000000", null, null, null,
                        new TypeSet("VOICE", "PREF"), null);
    }

    private void testV21Japanese1Common(int resId, int vcardType, boolean japanese) {
        mVerifier.initForImportTest(vcardType, resId);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "\u5B89\u85E4\u30ED\u30A4\u30C9")
                .put(StructuredName.DISPLAY_NAME, "\u5B89\u85E4\u30ED\u30A4\u30C9")
                // While vCard parser does not split "SOUND" property values,
                // ContactStruct care it.
                .put(StructuredName.PHONETIC_GIVEN_NAME,
                        "\uFF71\uFF9D\uFF84\uFF9E\uFF73\uFF9B\uFF72\uFF84\uFF9E");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                // Phone number formatting is different.
                .put(Phone.NUMBER, (japanese ? "03-0000-0000" : "030-000-0000"))
                .put(Phone.TYPE, Phone.TYPE_OTHER)
                .put(Phone.IS_PRIMARY, 1);
    }

    /**
     * Verifies vCard with Japanese can be parsed correctly with
     * {@link com.android.vcard.VCardConfig#VCARD_TYPE_V21_GENERIC}.
     */
    public void testV21Japanese1_Type_Generic_Utf8() {
        testV21Japanese1Common(
                R.raw.v21_japanese_1, VCardConfig.VCARD_TYPE_V21_GENERIC, false);
    }

    /**
     * Verifies vCard with Japanese can be parsed correctly with
     * {@link com.android.vcard.VCardConfig#VCARD_TYPE_V21_JAPANESE}.
     */
    public void testV21Japanese1_Type_Japanese_Sjis() {
        testV21Japanese1Common(
                R.raw.v21_japanese_1, VCardConfig.VCARD_TYPE_V21_JAPANESE, true);
    }

    /**
     * Verifies vCard with Japanese can be parsed correctly with
     * {@link com.android.vcard.VCardConfig#VCARD_TYPE_V21_JAPANESE}.
     * since vCard 2.1 specifies the charset of each line if it contains non-Ascii.
     */
    public void testV21Japanese1_Type_Japanese_Utf8() {
        testV21Japanese1Common(
                R.raw.v21_japanese_1, VCardConfig.VCARD_TYPE_V21_JAPANESE, true);
    }

    public void testV21Japanese2_Parsing() {
        mVerifier.initForImportTest(VCardConfig.VCARD_TYPE_V21_JAPANESE, R.raw.v21_japanese_2);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "\u5B89\u85E4;\u30ED\u30A4\u30C9\u0031;;;",
                        Arrays.asList("\u5B89\u85E4", "\u30ED\u30A4\u30C9\u0031",
                                "", "", ""),
                        null, mContentValuesForSJis, null, null)
                .addExpectedNodeWithOrder("FN", "\u5B89\u85E4\u0020\u30ED\u30A4\u30C9\u0020\u0031",
                        null, null, mContentValuesForSJis, null, null)
                .addExpectedNodeWithOrder("SOUND",
                        "\uFF71\uFF9D\uFF84\uFF9E\uFF73;\uFF9B\uFF72\uFF84\uFF9E\u0031;;;",
                        null, null, mContentValuesForSJis,
                        new TypeSet("X-IRMC-N"), null)
                .addExpectedNodeWithOrder("ADR",
                        ";\u6771\u4EAC\u90FD\u6E0B\u8C37\u533A\u685C" +
                        "\u4E18\u753A\u0032\u0036\u002D\u0031\u30BB" +
                        "\u30EB\u30EA\u30A2\u30F3\u30BF\u30EF\u30FC\u0036" +
                        "\u968E;;;;150-8512;",
                        Arrays.asList("",
                                "\u6771\u4EAC\u90FD\u6E0B\u8C37\u533A\u685C" +
                                "\u4E18\u753A\u0032\u0036\u002D\u0031\u30BB" +
                                "\u30EB\u30EA\u30A2\u30F3\u30BF\u30EF\u30FC" +
                                "\u0036\u968E", "", "", "", "150-8512", ""),
                        null, mContentValuesForQPAndSJis, new TypeSet("HOME"), null)
                .addExpectedNodeWithOrder("NOTE", "\u30E1\u30E2", null, null,
                        mContentValuesForQPAndSJis, null, null);
    }

    public void testV21Japanese2_Type_Generic_Utf8() {
        mVerifier.initForImportTest(V21, R.raw.v21_japanese_2);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "\u5B89\u85E4")
                .put(StructuredName.GIVEN_NAME, "\u30ED\u30A4\u30C9\u0031")
                .put(StructuredName.DISPLAY_NAME,
                        "\u5B89\u85E4\u0020\u30ED\u30A4\u30C9\u0020\u0031")
                // ContactStruct should correctly split "SOUND" property into several elements,
                // even though VCardParser side does not care it.
                .put(StructuredName.PHONETIC_FAMILY_NAME, "\uFF71\uFF9D\uFF84\uFF9E\uFF73")
                .put(StructuredName.PHONETIC_GIVEN_NAME, "\uFF9B\uFF72\uFF84\uFF9E\u0031");
        elem.addExpected(StructuredPostal.CONTENT_ITEM_TYPE)
                .put(StructuredPostal.POSTCODE, "150-8512")
                .put(StructuredPostal.STREET,
                        "\u6771\u4EAC\u90FD\u6E0B\u8C37\u533A\u685C" +
                        "\u4E18\u753A\u0032\u0036\u002D\u0031\u30BB" +
                        "\u30EB\u30EA\u30A2\u30F3\u30BF\u30EF\u30FC" +
                        "\u0036\u968E")
                .put(StructuredPostal.FORMATTED_ADDRESS,
                        "\u6771\u4EAC\u90FD\u6E0B\u8C37\u533A\u685C" +
                        "\u4E18\u753A\u0032\u0036\u002D\u0031\u30BB" +
                        "\u30EB\u30EA\u30A2\u30F3\u30BF\u30EF\u30FC" +
                        "\u0036\u968E 150-8512")
                .put(StructuredPostal.TYPE, StructuredPostal.TYPE_HOME);
        elem.addExpected(Note.CONTENT_ITEM_TYPE)
                .put(Note.NOTE, "\u30E1\u30E2");
    }

    public void testV21Japanese3_Parsing() {
        mVerifier.initForImportTest(VCardConfig.VCARD_TYPE_V21_JAPANESE, R.raw.v21_japanese_3);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "\u4f0a\u80fd;\u572d\u4e00;\u4f0a\u4e88;;",
                        Arrays.asList("\u4f0a\u80fd", "\u572d\u4e00", "\u4f0a\u4e88",
                                "", ""),
                        null, mContentValuesForSJis, null, null)
                .addExpectedNodeWithOrder("FN", "\u4f0a\u80fd\u0020\u572d\u4e00\u0020\u4f0a\u4e88",
                        null, null, mContentValuesForSJis, null, null);
    }

    public void testV21Japanese3_Parsing_Utf8() {
        mVerifier.initForImportTest(V21, R.raw.v21_japanese_3);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "\u4f0a\u80fd;\u572d\u4e00;\u4f0a\u4e88;;",
                        Arrays.asList("\u4f0a\u80fd", "\u572d\u4e00", "\u4f0a\u4e88",
                                "", ""),
                        null, mContentValuesForSJis, null, null)
                .addExpectedNodeWithOrder("FN", "\u4f0a\u80fd\u0020\u572d\u4e00\u0020\u4f0a\u4e88",
                        null, null, mContentValuesForSJis, null, null);
    }

    public void testV30Japanese_Parsing() {
        mVerifier.initForImportTest(V30, R.raw.v30_japanese);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "\u4f0a\u80fd;\u572d\u4e00;\u4f0a\u4e88;;",
                        Arrays.asList("\u4f0a\u80fd", "\u572d\u4e00", "\u4f0a\u4e88",
                                "", ""),
                        null, mContentValuesForSJis, null, null)
                .addExpectedNodeWithOrder("FN", "\u4f0a\u80fd\u0020\u572d\u4e00\u0020\u4f0a\u4e88",
                        null, null, mContentValuesForSJis, null, null);
    }

    public void testV21MultipleEntryCase_Parse() {
        mVerifier.initForImportTest(VCardConfig.VCARD_TYPE_V21_JAPANESE, R.raw.v21_multiple_entry);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "\u5B89\u85E4\u30ED\u30A4\u30C9\u0033;;;;",
                        Arrays.asList("\u5B89\u85E4\u30ED\u30A4\u30C9\u0033", "", "", "", ""),
                        null, mContentValuesForSJis, null, null)
                .addExpectedNodeWithOrder("SOUND",
                        "\uFF71\uFF9D\uFF84\uFF9E\uFF73\uFF9B\uFF72\uFF84\uFF9E\u0033;;;;",
                        null, null, mContentValuesForSJis,
                        new TypeSet("X-IRMC-N"), null)
                .addExpectedNodeWithOrder("TEL", "9", new TypeSet("X-NEC-SECRET"))
                .addExpectedNodeWithOrder("TEL", "10", new TypeSet("X-NEC-HOTEL"))
                .addExpectedNodeWithOrder("TEL", "11", new TypeSet("X-NEC-SCHOOL"))
                .addExpectedNodeWithOrder("TEL", "12", new TypeSet("FAX", "HOME"));
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "\u5B89\u85E4\u30ED\u30A4\u30C9\u0034;;;;",
                        Arrays.asList("\u5B89\u85E4\u30ED\u30A4\u30C9\u0034", "", "", "", ""),
                        null, mContentValuesForSJis, null, null)
                .addExpectedNodeWithOrder("SOUND",
                        "\uFF71\uFF9D\uFF84\uFF9E\uFF73\uFF9B\uFF72\uFF84\uFF9E\u0034;;;;",
                        null, null, mContentValuesForSJis,
                        new TypeSet("X-IRMC-N"), null)
                .addExpectedNodeWithOrder("TEL", "13", new TypeSet("MODEM"))
                .addExpectedNodeWithOrder("TEL", "14", new TypeSet("PAGER"))
                .addExpectedNodeWithOrder("TEL", "15", new TypeSet("X-NEC-FAMILY"))
                .addExpectedNodeWithOrder("TEL", "16", new TypeSet("X-NEC-GIRL"));
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "\u5B89\u85E4\u30ED\u30A4\u30C9\u0035;;;;",
                        Arrays.asList("\u5B89\u85E4\u30ED\u30A4\u30C9\u0035", "", "", "", ""),
                        null, mContentValuesForSJis, null, null)
                .addExpectedNodeWithOrder("SOUND",
                        "\uFF71\uFF9D\uFF84\uFF9E\uFF73\uFF9B\uFF72\uFF84\uFF9E\u0035;;;;",
                        null, null, mContentValuesForSJis,
                        new TypeSet("X-IRMC-N"), null)
                .addExpectedNodeWithOrder("TEL", "17", new TypeSet("X-NEC-BOY"))
                .addExpectedNodeWithOrder("TEL", "18", new TypeSet("X-NEC-FRIEND"))
                .addExpectedNodeWithOrder("TEL", "19", new TypeSet("X-NEC-PHS"))
                .addExpectedNodeWithOrder("TEL", "20", new TypeSet("X-NEC-RESTAURANT"));
    }

    public void testV21MultipleEntryCase() {
        mVerifier.initForImportTest(VCardConfig.VCARD_TYPE_V21_JAPANESE, R.raw.v21_multiple_entry);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "\u5B89\u85E4\u30ED\u30A4\u30C9\u0033")
                .put(StructuredName.DISPLAY_NAME, "\u5B89\u85E4\u30ED\u30A4\u30C9\u0033")
                .put(StructuredName.PHONETIC_GIVEN_NAME,
                        "\uFF71\uFF9D\uFF84\uFF9E\uFF73\uFF9B\uFF72\uFF84\uFF9E\u0033");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "NEC-SECRET")
                .put(Phone.NUMBER, "9");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "NEC-HOTEL")
                .put(Phone.NUMBER, "10");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "NEC-SCHOOL")
                .put(Phone.NUMBER, "11");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_FAX_HOME)
                .put(Phone.NUMBER, "12");

        elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "\u5B89\u85E4\u30ED\u30A4\u30C9\u0034")
                .put(StructuredName.DISPLAY_NAME, "\u5B89\u85E4\u30ED\u30A4\u30C9\u0034")
                .put(StructuredName.PHONETIC_GIVEN_NAME,
                        "\uFF71\uFF9D\uFF84\uFF9E\uFF73\uFF9B\uFF72\uFF84\uFF9E\u0034");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "MODEM")
                .put(Phone.NUMBER, "13");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_PAGER)
                .put(Phone.NUMBER, "14");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "NEC-FAMILY")
                .put(Phone.NUMBER, "15");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "NEC-GIRL")
                .put(Phone.NUMBER, "16");

        elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "\u5B89\u85E4\u30ED\u30A4\u30C9\u0035")
                .put(StructuredName.DISPLAY_NAME, "\u5B89\u85E4\u30ED\u30A4\u30C9\u0035")
                .put(StructuredName.PHONETIC_GIVEN_NAME,
                        "\uFF71\uFF9D\uFF84\uFF9E\uFF73\uFF9B\uFF72\uFF84\uFF9E\u0035");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "NEC-BOY")
                .put(Phone.NUMBER, "17");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "NEC-FRIEND")
                .put(Phone.NUMBER, "18");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "NEC-PHS")
                .put(Phone.NUMBER, "19");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "NEC-RESTAURANT")
                .put(Phone.NUMBER, "20");
    }

    public void testIgnoreAgentV21_Parse() {
        mVerifier.initForImportTest(V21, R.raw.v21_winmo_65);
        ContentValues contentValuesForValue = new ContentValues();
        contentValuesForValue.put("VALUE", "DATE");
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", Arrays.asList("Example", "", "", "", ""))
                .addExpectedNodeWithOrder("FN", "Example")
                .addExpectedNodeWithOrder("ANNIVERSARY", "20091010", contentValuesForValue)
                .addExpectedNodeWithOrder("AGENT", "")
                .addExpectedNodeWithOrder("X-CLASS", "PUBLIC")
                .addExpectedNodeWithOrder("X-REDUCTION", "")
                .addExpectedNodeWithOrder("X-NO", "");
    }

    public void testIgnoreAgentV21() {
        mVerifier.initForImportTest(V21, R.raw.v21_winmo_65);
        ContentValuesVerifier verifier = new ContentValuesVerifier();
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "Example")
                .put(StructuredName.DISPLAY_NAME, "Example");
        elem.addExpected(Event.CONTENT_ITEM_TYPE)
                .put(Event.TYPE, Event.TYPE_ANNIVERSARY)
                .put(Event.START_DATE, "20091010");
    }

    public void testTolerateInvalidCommentLikeLineV21() {
        mVerifier.initForImportTest(V21, R.raw.v21_invalid_comment_line);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.GIVEN_NAME, "Conference Call")
                .put(StructuredName.DISPLAY_NAME, "Conference Call");
        elem.addExpected(Note.CONTENT_ITEM_TYPE)
                .put(Note.NOTE, "This is an (sharp ->#<- sharp) example. "
                        + "This message must NOT be ignored.");
    }

    public void testPagerV30_Parse() {
        mVerifier.initForImportTest(V30, R.raw.v30_pager);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", Arrays.asList("F", "G", "M", "", ""))
                .addExpectedNodeWithOrder("TEL", "6101231234@pagersample.com",
                        new TypeSet("WORK", "MSG", "PAGER"));
    }

    public void testPagerV30() {
        mVerifier.initForImportTest(V30, R.raw.v30_pager);
        ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "F")
                .put(StructuredName.MIDDLE_NAME, "M")
                .put(StructuredName.GIVEN_NAME, "G")
                .put(StructuredName.DISPLAY_NAME, "G M F");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_PAGER)
                .put(Phone.NUMBER, "6101231234@pagersample.com");
    }

    public void testMultiBytePropV30_Parse() {
        mVerifier.initForImportTest(V30, R.raw.v30_multibyte_param);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", Arrays.asList("F", "G", "M", "", ""))
                .addExpectedNodeWithOrder("TEL", "1", new TypeSet("\u8D39"));
    }

    public void testMultiBytePropV30() {
        mVerifier.initForImportTest(V30, R.raw.v30_multibyte_param);
        final ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "F")
                .put(StructuredName.MIDDLE_NAME, "M")
                .put(StructuredName.GIVEN_NAME, "G")
                .put(StructuredName.DISPLAY_NAME, "G M F");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "\u8D39")
                .put(Phone.NUMBER, "1");
    }

    public void testCommaSeparatedParamsV30_Parse() {
        mVerifier.initForImportTest(V30, R.raw.v30_comma_separated);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", Arrays.asList("F", "G", "M", "", ""),
                        new TypeSet("PREF", "HOME"))
                .addExpectedNodeWithOrder("TEL", "1",
                        new TypeSet("COMMA,SEPARATED:INSIDE.DQUOTE", "PREF"));
    }

    public void testSortAsV40_Parse() {
        mVerifier.initForImportTest(V40, R.raw.v40_sort_as);

        final ContentValues contentValuesForSortAsN = new ContentValues();
        contentValuesForSortAsN.put("SORT-AS",
                "\u3042\u3093\u3069\u3046;\u308D\u3044\u3069");
        final ContentValues contentValuesForSortAsOrg = new ContentValues();
        contentValuesForSortAsOrg.put("SORT-AS",
                "\u3050\u30FC\u3050\u308B;\u3051\u3093\u3055\u304F\u3076\u3082\u3093");

        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("FN", "\u5B89\u85E4\u0020\u30ED\u30A4\u30C9")
                .addExpectedNodeWithOrder("N",
                        Arrays.asList("\u5B89\u85E4", "\u30ED\u30A4\u30C9", "", "", ""),
                        contentValuesForSortAsN)
                .addExpectedNodeWithOrder("ORG",
                        Arrays.asList("\u30B0\u30FC\u30B0\u30EB", "\u691C\u7D22\u90E8\u9580"),
                        contentValuesForSortAsOrg, new TypeSet("WORK"));
    }

    public void testSortAsV40() {
        mVerifier.initForImportTest(V40, R.raw.v40_sort_as);
        final ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "\u5B89\u85E4")
                .put(StructuredName.GIVEN_NAME, "\u30ED\u30A4\u30C9")
                .put(StructuredName.DISPLAY_NAME, "\u5B89\u85E4\u0020\u30ED\u30A4\u30C9")
                .put(StructuredName.PHONETIC_FAMILY_NAME, "\u3042\u3093\u3069\u3046")
                .put(StructuredName.PHONETIC_GIVEN_NAME,
                        "\u308D\u3044\u3069");
        elem.addExpected(Organization.CONTENT_ITEM_TYPE)
                .put(Organization.TYPE, Organization.TYPE_WORK)
                .put(Organization.COMPANY, "\u30B0\u30FC\u30B0\u30EB")
                .put(Organization.DEPARTMENT, "\u691C\u7D22\u90E8\u9580")
                .put(Organization.PHONETIC_NAME,
                        "\u3050\u30FC\u3050\u308B\u3051\u3093\u3055\u304F\u3076\u3082\u3093");
    }

    public void testIMV21_Parse() {
        mVerifier.initForImportTest(V21, R.raw.v21_im);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("X-ANDROID-CUSTOM",
                        Arrays.asList("vnd.android.cursor.item/nickname", "Nick", "1",
                                "", "", "", "", "", "", "", "", "", "", "", "", ""))  // 13
                .addExpectedNodeWithOrder("X-GOOGLE-TALK", "hhh@gmail.com");
    }

    public void testIMV21() {
        mVerifier.initForImportTest(V21, R.raw.v21_im);
        final ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(Nickname.CONTENT_ITEM_TYPE)
                .put(Nickname.NAME, "Nick")
                .put(Nickname.TYPE, "1");
        elem.addExpected(Im.CONTENT_ITEM_TYPE)
                .put(Im.PROTOCOL, Im.PROTOCOL_GOOGLE_TALK)
                .put(Im.TYPE, Im.TYPE_HOME)
                .put(Im.DATA, "hhh@gmail.com");
    }

    public void testSipV30_Parse() {
        mVerifier.initForImportTest(V30, R.raw.v30_sip);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("FN", "Android")
                .addExpectedNodeWithOrder("IMPP", "sip:android@android.example.com",
                        new TypeSet("personal"));
    }

    public void testSipV30() {
        mVerifier.initForImportTest(V30, R.raw.v30_sip);
        final ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.DISPLAY_NAME, "Android");
        // Type is ignored silently.
        elem.addExpected(SipAddress.CONTENT_ITEM_TYPE)
                .put(SipAddress.TYPE, SipAddress.TYPE_CUSTOM)
                .put(SipAddress.LABEL, "personal")
                .put(SipAddress.SIP_ADDRESS, "android@android.example.com");
    }

    public void testSipV21_Parse() {
        mVerifier.initForImportTest(V21, R.raw.v21_sip);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("FN", "Android")
                .addExpectedNodeWithOrder("X-SIP", "888")
                .addExpectedNodeWithOrder("X-SIP", "sip:90-180-360");
    }

    public void testSipV21() {
        mVerifier.initForImportTest(V21, R.raw.v21_sip);
        final ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.DISPLAY_NAME, "Android");
        elem.addExpected(SipAddress.CONTENT_ITEM_TYPE)
                .put(SipAddress.TYPE, SipAddress.TYPE_OTHER)
                .put(SipAddress.SIP_ADDRESS, "888");
        // "sip:" should be removed.
        elem.addExpected(SipAddress.CONTENT_ITEM_TYPE)
                .put(SipAddress.TYPE, SipAddress.TYPE_OTHER)
                .put(SipAddress.SIP_ADDRESS, "90-180-360");
    }

    public void testSipV40() {
        mVerifier.initForImportTest(V40, R.raw.v40_sip);
        final ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "\u5B89\u85E4")
                .put(StructuredName.GIVEN_NAME, "\u30ED\u30A4\u30C9")
                .put(StructuredName.DISPLAY_NAME, "\u5B89\u85E4\u0020\u30ED\u30A4\u30C9");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_HOME)
                .put(Phone.NUMBER, "1");
        elem.addExpected(SipAddress.CONTENT_ITEM_TYPE)
                .put(SipAddress.TYPE, SipAddress.TYPE_HOME)
                .put(SipAddress.SIP_ADDRESS, "example@example.com");
    }

    public void testCustomPropertyV21_Parse() {
        mVerifier.initForImportTest(V21, R.raw.v21_x_param);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "Ando;Roid;", Arrays.asList("Ando", "Roid", ""))
                .addExpectedNodeWithOrder("ADR", "pobox;street", Arrays.asList("pobox", "street"),
                        new TypeSet("X-custom"))
                .addExpectedNodeWithOrder("TEL", "1", new TypeSet("X-CuStoMpRop"))
                .addExpectedNodeWithOrder("TEL", "2", new TypeSet("custompropertywithoutx"))
                .addExpectedNodeWithOrder("EMAIL", "email@example.com",
                        new TypeSet("X-cUstomPrOperty"))
                .addExpectedNodeWithOrder("EMAIL", "email2@example.com",
                        new TypeSet("CUSTOMPROPERTYWITHOUTX"));
    }

    public void testCustomPropertyV21() {
        mVerifier.initForImportTest(V21, R.raw.v21_x_param);
        final ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "Ando")
                .put(StructuredName.GIVEN_NAME, "Roid")
                .put(StructuredName.DISPLAY_NAME, "Roid Ando");
        elem.addExpected(StructuredPostal.CONTENT_ITEM_TYPE)
                .put(StructuredPostal.TYPE, StructuredPostal.TYPE_CUSTOM)
                .put(StructuredPostal.LABEL, "custom")
                .put(StructuredPostal.POBOX, "pobox")
                .put(StructuredPostal.STREET, "street")
                .put(StructuredPostal.FORMATTED_ADDRESS, "pobox street");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "CuStoMpRop")
                .put(Phone.NUMBER, "1");
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_CUSTOM)
                .put(Phone.LABEL, "custompropertywithoutx")
                .put(Phone.NUMBER, "2");
        elem.addExpected(Email.CONTENT_ITEM_TYPE)
                .put(Email.TYPE, Email.TYPE_CUSTOM)
                .put(Email.LABEL, "cUstomPrOperty")
                .put(Email.ADDRESS, "email@example.com");
        elem.addExpected(Email.CONTENT_ITEM_TYPE)
                .put(Email.TYPE, Email.TYPE_CUSTOM)
                .put(Email.LABEL, "CUSTOMPROPERTYWITHOUTX")
                .put(Email.ADDRESS, "email2@example.com");
    }

    public void testBase64Without2CrLf_Parse() {
        mVerifier.initForImportTest(V21, R.raw.v21_base64_no_2_crlf);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "name")
                .addExpectedNodeWithOrder("FN", "fullname")
                .addExpectedNodeWithOrder("PHOTO", null,
                        null, sPhotoByteArrayForComplicatedCase, mContentValuesForBase64V21,
                        new TypeSet("JPEG"), null);
    }

    public void testBase64Without2CrLfForBlackBerry_Parse() {
        mVerifier.initForImportTest(V21, R.raw.v21_blackberry_photo);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("FN", "boogie")
                .addExpectedNodeWithOrder("N", "boogie")
                .addExpectedNodeWithOrder("PHOTO", null,
                        null, sPhotoByteArrayForComplicatedCase, mContentValuesForBase64V21,
                        null, null)
                .addExpectedNodeWithOrder("TEL", "+5555555", new TypeSet("WORK"))
                .addExpectedNodeWithOrder("TEL", "+5555556", new TypeSet("CELL"))
                .addExpectedNodeWithOrder("EMAIL", "forrestgump@walladalla.com",
                        new TypeSet("INTERNET"));
    }

    public void testMalformedBase64PhotoThrowsVCardException() {
        mVerifier.initForImportTest(V21, R.raw.v21_malformed_photo);

        String expectedMsgContent = "qgEPAAIAAAAHAAAAugEQAAIAAAAG:ASDF==";
        mVerifier.addVCardExceptionVerifier(expectedMsgContent);
    }

    public void testAndroidCustomPropertyV21() {
        mVerifier.initForImportTest(V21, R.raw.v21_android_custom_prop);
        final ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected("custom_mime1")
                .put("data1", "1").put("data2", "2").put("data3", "3").put("data4", "4")
                .put("data5", "5").put("data6", "6").put("data7", "7").put("data8", "8")
                .put("data9", "9").put("data10", "10").put("data11", "11").put("data12", "12")
                .put("data13", "13").put("data14", "14").put("data15", "15");
        // 16'th elemnt ('p') should be ignored
        elem.addExpected("custom_mime2")
                .put("data1", "a").put("data2", "b").put("data3", "c").put("data4", "d")
                .put("data5", "e").put("data6", "f").put("data7", "g").put("data8", "h")
                .put("data9", "i").put("data10", "j").put("data11", "k").put("data12", "l")
                .put("data13", "m").put("data14", "n").put("data15", "o");

        // custom_mime3 shouldn't be here, as there's no data

        // Smoke test.
        elem.addExpected("custom_mime4").put("data1", "z");
    }

    public void testPauseWaitV30_Parse() {
        mVerifier.initForImportTest(V30, R.raw.v30_pause_wait);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("FN", "Pause Wait")
                .addExpectedNodeWithOrder("N", "Pause;Wait;;;",
                        Arrays.asList("Pause", "Wait", "", "", ""))
                .addExpectedNodeWithOrder("TEL", "p1234p5678w9");
     }

    public void testPauseWaitV30() {
        mVerifier.initForImportTest(V30, R.raw.v30_pause_wait);
        final ContentValuesVerifierElem elem = mVerifier.addContentValuesVerifierElem();
        elem.addExpected(StructuredName.CONTENT_ITEM_TYPE)
                .put(StructuredName.FAMILY_NAME, "Pause")
                .put(StructuredName.GIVEN_NAME, "Wait")
                .put(StructuredName.DISPLAY_NAME, "Pause Wait");
        // See PhoneNumberUtils in Android SDK.
        elem.addExpected(Phone.CONTENT_ITEM_TYPE)
                .put(Phone.TYPE, Phone.TYPE_OTHER)
                .put(Phone.NUMBER, ",1234,5678;9");
    }

    // We ran into a case where a 2.1 vcard was sent with a 3.0+ property,
    // X-ANDROID-CUSTOM. If that property followed photo data without a blank line,
    // getBase64() would append the X-ANDROID-CUSTOM data line to the photo data.
    // When Base64.decode() was called with this data, it would throw an exception.
    // Besides looking for the normal 2.1 properties, we need to look for
    // X-ANDROID-CUSTOM as well. Here's an example of a bad vcard that would cause
    // this problem (This vcard was generated by a KLP MR2 Nexus 5 or a KLP Moto X):
    // ...
    //  LHnTW/l/wB9f+PV2UcFyfGeVis3qVPch7pWjs/s+m/2Zpdp5f8Ayy/df6uPZWNcR9dRzWoZPb
    //  /l4/xpotvtH2vj/l39f8/99f8AxFdvKjwD/9k=
    // X-ANDROID-CUSTOM:vnd.android.cursor.item/contact_event;1999-07-10;1;;;;;;;;;;;;;
    // BDAY:1975-08-20
    // END:VCARD
    //
    // Here's how to build and run the tests in this file:
    //   mmm frameworks/opt/vcard
    //   adb install -r -d out/target/product/hammerhead/data/app/AndroidVCardTests.apk
    //   adb shell am instrument -w com.android.vcard.tests/android.test.InstrumentationTestRunner
    //
    public void testV21XAndroidCustomAfterPhoto() {
        mVerifier.initForImportTest(V21, R.raw.v21_x_android_custom_after_photo);
        mVerifier.addPropertyNodesVerifierElem()
                .addExpectedNodeWithOrder("N", "Ando;Roid;", Arrays.asList("Ando", "Roid", ""))
                .addExpectedNodeWithOrder("PHOTO", null,
                        null, sPhotoByteArrayForComplicatedCase, mContentValuesForBase64V21,
                        null, null)
                .addExpectedNodeWithOrder("X-ANDROID-CUSTOM",
                        "vnd.android.cursor.item/contact_event;1999-07-10;1;;;;;;;;;;;;;")
                .addExpectedNodeWithOrder("BDAY", "1975-08-20");
    }

}
