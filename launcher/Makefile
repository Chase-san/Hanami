CC = gcc
CFLAGS = -std=gnu99 -Wall -O3 -g0
LFLAGS = -s -mwindows -static-libgcc $(CFLAGS)
OBJECTS = $(patsubst %.c, %.o, $(wildcard *.c)) $(patsubst %.rc, %.res, $(wildcard *.rc))
TARGET = hanami.exe

default: $(TARGET)

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

%.res: %.rc
	windres $< -O coff -o $@

$(TARGET): $(OBJECTS)
	$(CC) $(OBJECTS) $(LFLAGS) -Wall -o $@
	

clean:
	rm -f *.o
	rm -f *.res
	rm -f $(TARGET)