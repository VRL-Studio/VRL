/*
 * Copyright 2013 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 * 
 * # WHAT DOES THIS FILE?
 *
 * Runs the specified comand with root previleges.
 * CAUTION: may break your system! Be careful!
 *
 * # CREDITS
 *
 * used tutorial code by Michael V. O'Brien to learn how to do this 
 * see: https://github.com/michaelvobrien/OSXSlightlyBetterAuth
 */


#import <Foundation/Foundation.h>
// Add Security.framework to the Xcode project

int runAsRoot(const char* command, const char * argv[]);

int main (int argc, const char * argv[]) {
    
    const char* noArgs[] = {NULL};
    
    if (argc < 2) {
        fprintf( stderr, "wrong number of arguments" );
        return 1;
    }
    
    if (argc == 3) {
            
        NSArray* array = [[NSString stringWithUTF8String: argv[2]] componentsSeparatedByString:@" "];
    
        int count = array.count;
        const char **carray = (const char **) malloc(sizeof(const char *) * (count + 1));
    
        int i;
        for(i = 0; i < count; i++) {
            NSString *s = [array objectAtIndex:i];//get a NSString
            const char *cstr = [s cStringUsingEncoding:NSUTF8StringEncoding];//get cstring
            int len = strlen(cstr);//get its length
            char *cstr_copy = (char *) malloc(sizeof(char) * (len + 1));//allocate memory, + 1 for ending '\0'
            strcpy(cstr_copy, cstr);//make a copy
            carray[i] = cstr_copy;//put the point in cargs
        }
    
        carray[i] = NULL;
        
        runAsRoot(argv[1], carray);
        
    } else {
        
        runAsRoot(argv[1], noArgs);
    }
    
    return 0;
}

int runAsRoot(const char* command, const char * argv[]) {
	
    OSStatus status;  // http://developer.apple.com/documentation/Security/Reference/authorization_ref/Reference/reference.html#//apple_ref/doc/uid/TP30000826-CH4g-CJBEABHG
    AuthorizationRef authorizationRef;
	
    // AuthorizationCreate and pass NULL as the initial AuthorizationRights set so that the AuthorizationRef
    // gets created successfully, and then later call AuthorizationCopyRights to determine or extend the allowable rights.
    // http://developer.apple.com/qa/qa2001/qa1172.html
    status = AuthorizationCreate(NULL, kAuthorizationEmptyEnvironment, kAuthorizationFlagDefaults, &authorizationRef);
    if (status != errAuthorizationSuccess)
        NSLog(@"Error Creating Initial Authorization: %d", status);
	
    // kAuthorizationRightExecute == "system.privilege.admin"
    AuthorizationItem right = {kAuthorizationRightExecute, 0, NULL, 0};
    AuthorizationRights rights = {1, &right};
    AuthorizationFlags flags = kAuthorizationFlagDefaults |
	kAuthorizationFlagInteractionAllowed |
	kAuthorizationFlagPreAuthorize |
	kAuthorizationFlagExtendRights;
	
    // Call AuthorizationCopyRights to determine or extend the allowable rights.
    status = AuthorizationCopyRights(authorizationRef, &rights, NULL, flags, NULL);
    if (status != errAuthorizationSuccess)
        NSLog(@"Copy Rights Unsuccessful: %d", status);
	
    // EXAMPLE 1: This system tool should work as intended. NOTE: The
    // do-while was used to create scope rather than a function to
    // make this demonstration code read more top-down.
    {
    const char *tool = command;
    FILE *pipe = NULL;
    
    status = AuthorizationExecuteWithPrivileges(authorizationRef, tool, kAuthorizationFlagDefaults, argv, &pipe);
    if (status != errAuthorizationSuccess)
        NSLog(@"Error: %d", status);
    
    char readBuffer[128];
    if (status == errAuthorizationSuccess) {
        for (;;) {
            int bytesRead = read(fileno(pipe), readBuffer, sizeof(readBuffer));
            if (bytesRead < 1) break;
            write(fileno(stdout), readBuffer, bytesRead);
        }
    }
    } 
	
    
    // The only way to guarantee that a credential acquired when you request a right
    // is not shared with other authorization instances is to destroy the credential.
    // To do so, call the AuthorizationFree function with the flag kAuthorizationFlagDestroyRights.
    // http://developer.apple.com/documentation/Security/Conceptual/authorization_concepts/02authconcepts/chapter_2_section_7.html
    status = AuthorizationFree(authorizationRef, kAuthorizationFlagDestroyRights);

}
