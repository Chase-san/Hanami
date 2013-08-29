#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <wchar.h>
#include <windows.h>

/*
 * Poor Mans Windows Java Unicode Command Line Passer.
 * Easily uses up to 4kb of stack.
 */
int main() {
	int argc;
	wchar_t** argv = CommandLineToArgvW(GetCommandLineW(),&argc);
	
	wchar_t wjargs[1024];
	
	wcscat(wjargs,L"-jar ");
	{
		wchar_t path[1024];
		wmemset(path,0,1024);
		GetModuleFileNameW(NULL,path,1024);
		//find last occurance of \, replace it with 0 (terminator)
		for(int i=1023;i>=0;--i) {
			if((int)path[i]==92) {
				path[i] = 0;
				break;
			}
		}
		wcscat(wjargs,path);
	}
	//our jar file
	wcscat(wjargs,L"\\hanami.jar");
	
	if(argc > 1) {
		int len = wcslen(argv[1]);
		wchar_t buf[6];
		//tell the program we are going to encode it in hex bytes
		wcscat(wjargs,L" -x ");
		
		//TODO replace this with Base 64 UTF-8.
		for(int i=0;i<len;++i) {
			swprintf(buf,L"%04x",(int)argv[1][i]);
			wcscat(wjargs,buf);
		}
	}
	
	//TODO check if they even have java installed, if not, yell at them.
	ShellExecuteW(NULL,L"open",L"javaw.exe",wjargs,NULL,SW_SHOWNORMAL);
	
	//Sleep for 5 or so seconds. Some programs delete temporary files after that program closes, which means
	//to open these temporary files, we need to stay open for a few extra seconds.
	Sleep(5000);
	
	return 0;
}