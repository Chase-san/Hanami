#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <wchar.h>
#include <windows.h>

//comment this out to use Hex encoding
#define BASE64 1

#ifdef BASE64
static char b64_table[] = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
		'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
		'w', 'x', 'y', 'z', '0', '1', '2', '3',
		'4', '5', '6', '7', '8', '9', '+', '/' };

static int mod_table[] = { 0, 2, 1 };

char *base64_encode(const unsigned char *data, size_t input_length, size_t *output_length) {
	*output_length = ((input_length - 1) / 3) * 4 + 4;

	char *en = malloc(*output_length + 1);
	if (en == NULL)
		return NULL;
	
	for (int i = 0, j = 0; i < input_length;) {
		uint32_t a = i < input_length ? data[i++] : 0;
		uint32_t b = i < input_length ? data[i++] : 0;
		uint32_t c = i < input_length ? data[i++] : 0;
		uint32_t v = (a << 0x10) + (b << 0x08) + c;
		en[j++] = b64_table[(v >> 3 * 6) & 0x3F];
		en[j++] = b64_table[(v >> 2 * 6) & 0x3F];
		en[j++] = b64_table[(v >> 1 * 6) & 0x3F];
		en[j++] = b64_table[(v >> 0 * 6) & 0x3F];
	}
	
	for (int i = 0; i < mod_table[input_length % 3]; i++)
		en[*output_length - 1 - i] = '=';
	
	en[*output_length] = 0;
	return en;
}
#endif

/*
 * Poor Mans Windows Java Unicode Command Line Passer.
 * Easily uses up to 4kb of stack.
 */
int main() {
	int argc;
	wchar_t** argv = CommandLineToArgvW(GetCommandLineW(),&argc);
	
	wchar_t wjargs[1024];
	
	wcscat(wjargs,L"-Xmx1024m -jar \"");
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
	wcscat(wjargs,L"\\lib\\hanami.jar\"");
	
	if(argc > 1) {
#ifdef BASE64
		//every 3 bytes into 4 bytes
		//Base 64 Encode
		
		//tell the program we are going to encode it in base64
		wcscat(wjargs,L" -64 ");
		
		size_t wlen = wcslen(argv[1]) * sizeof(wchar_t);
		size_t blen = 0;
		char *b = base64_encode((unsigned char*)argv[1], wlen, &blen);
		
		wchar_t wbuf[blen + 1];
		mbstowcs(wbuf,b,blen);
		free(b);
		wbuf[blen] = 0;
		
		wcscat(wjargs,wbuf);
#else
		//every 2 bytes into 4 bytes
		//Hex Byte Encode
		int len = wcslen(argv[1]);
		wchar_t buf[6];
		
		//tell the program we are going to encode it in hex bytes
		wcscat(wjargs,L" -x ");
		
		for(int i=0;i<len;++i) {
			swprintf(buf,L"%04x",(int)argv[1][i]);
			wcscat(wjargs,buf);
		}
#endif
	}
	
	//TODO check if they even have java installed, if not, yell at them.
	ShellExecuteW(NULL,L"open",L"javaw.exe",wjargs,NULL,SW_SHOWNORMAL);
	
	//Sleep for 5 or so seconds. Some programs delete temporary files after that program closes, which means
	//to open these temporary files, we need to stay open for a few extra seconds.
	Sleep(5000);
	
	return 0;
}