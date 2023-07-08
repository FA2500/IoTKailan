import network
import socket
import math
import urequests
import ujson
import machine
import time
import utime
import ntptime
import uos
import json
import ubinascii
import rp2
import array

from machine import Pin
from rp2 import PIO, StateMachine, asm_pio

waterLevelLow = 0

NUM_LEDS = 1
err_try=0

@asm_pio(sideset_init=PIO.OUT_LOW, out_shiftdir=PIO.SHIFT_LEFT,
autopull=True, pull_thresh=24)
def ws2812():
    T1 = 2
    T2 = 5
    T3 = 3
    label("bitloop")
    out(x, 1) .side(0) [T3 - 1]
    jmp(not_x, "do_zero") .side(1) [T1 - 1]
    jmp("bitloop") .side(1) [T2 - 1]
    label("do_zero")
    nop() .side(0) [T2 - 1]
    
sm = StateMachine(0, ws2812, freq=8000000, sideset_base=Pin(28))
sm.active(1)
ar = array.array("I", [0 for _ in range(NUM_LEDS)])

#print("red")
for i in range(NUM_LEDS):
    ar[i] = 255<<8          # shift 8 bits to the left
sm.put(ar,8)
#time.sleep_ms(100)

# Define Wi-Fi network credentials
#ssid = "HKKAILAN"
#password = "kailan123"

ssid="IOTCS"
password="Uitmcsiot@2023"

# Define Sensors
buzzer = machine.PWM(machine.Pin(18))

sta_if = network.WLAN(network.STA_IF)

def connectWifi():
    # Connect to Wi-Fi
    sta_if.active(True)
    sta_if.connect(ssid, password)
    while not sta_if.isconnected():
        #print("white")
        for i in range(NUM_LEDS):
            ar[i] = 0xFFFFFF
        sm.put(ar,8)
        #time.sleep_ms(100)
        pass
    ip=sta_if.ifconfig()[0]
    print('IP: ', ip)
    print("Wi-Fi connected")
    print("MAC 1 = "+ubinascii.hexlify(network.WLAN().config('mac'),':').decode())
    machine.Pin(28).value(1)
    ntptime.settime()
    print(time.localtime())
    #buzzer.freq(250)                
    #buzzer.duty_u16(19660)
    #utime.sleep(0.15)
    #buzzer.duty_u16(0)

uart = machine.UART(0, 9600, tx=machine.Pin(0), rx=machine.Pin(1))

database_url = "https://iot-hkkailan-default-rtdb.asia-southeast1.firebasedatabase.app/"
auth_token = "16XAPPIWoHLLLwCo8y4lgMeiRpjLaNMKxo9NzCNU"

connectWifi()
#buzzer.freq(250)                
#buzzer.duty_u16(19660)
#utime.sleep(0.15)
#buzzer.duty_u16(0)

while True:
    try:
        if not sta_if.isconnected():
            print("WIFI DC,RECONNECTING")
            connectWifi()
        if uart.any():
            data = uart.read(200)
            data = data.decode('utf-8')
            #print("green")
            for i in range(NUM_LEDS):
                ar[i] = 255<<16         # shift 16 bits to the left
            sm.put(ar,8)
            #time.sleep_ms(100)
            if data != "Ready":
                dict = json.loads(data)
                print(dict)
                time.sleep(1)
                request = urequests.patch(database_url + "sensor/pico.json?auth=" + auth_token, data=ujson.dumps(dict))
                time.sleep(1)
                request.close()
                time.sleep(1)
                if(int(dict["moisture"])>=425):
                    print("WATERPUMP1")
                    uart.write('pump1'.encode('utf-8'))
                if (int(dict["humidity1"])>=85):
                    print("FAN")
                    uart.write("fanon".encode('utf-8'))
                #4 hours
                    
                #4 hours
                if dict["waterlevel"] == "0" and waterLevelLow < 3:
                    waterreq = urequests.get("https://asia-southeast1-iot-hkkailan.cloudfunctions.net/lowWaterLevel")
                    time.sleep(1)
                    waterreq.close()
                    waterLevelLow = waterLevelLow + 1
                    time.sleep(1)

                time.sleep(1)
                try:
                    request2 = urequests.get(database_url+"control.json?auth="+auth_token)
                    time.sleep(1)
                    response2=json.loads(request2.text)
                    for i in range(NUM_LEDS):
                        ar[i] = 255
                    sm.put(ar,8)
                    if response2["fan"] == True:
                        uart.write("fanon".encode('utf-8'))
                        try:
                            dict2 = {'fan': False}
                            dict3 = json.loads(json.dumps(dict2))
                            request3 = urequests.patch(database_url + "control.json?auth=" + auth_token, data=ujson.dumps(dict3))
                            time.sleep(1)
                            request3.close()
                            time.sleep(1)
                        except Exception as e:
                            print("Error send fan data")
                            print(e)
                            time.sleep(1)
                    if response2["servo"] == True:
                        uart.write("servo".encode('utf-8'))
                        try:
                            dict2 = {'servo': False}
                            dict3 = json.loads(json.dumps(dict2))
                            request3 = urequests.patch(database_url + "control.json?auth=" + auth_token, data=ujson.dumps(dict3))
                            time.sleep(1)
                            request3.close()
                            time.sleep(1)
                        except Exception as e:
                            print("Error send servo data")
                            print(e)
                            time.sleep(1)
                    if response2["waterpump1"] == True:
                        uart.write("pump1".encode('utf-8'))
                        try:
                            dict2 = {'waterpump1': False}
                            dict3 = json.loads(json.dumps(dict2))
                            request3 = urequests.patch(database_url + "control.json?auth=" + auth_token, data=ujson.dumps(dict3))
                            time.sleep(1)
                            request3.close()
                            time.sleep(1)
                        except Exception as e:
                            print("Error send waterpump1 data")
                            print(e)
                            time.sleep(1)
                    if response2["waterpump2"] == True:
                        uart.write("pump2".encode('utf-8'))
                        try:
                            dict2 = {'waterpump2': False}
                            dict3 = json.loads(json.dumps(dict2))
                            request3 = urequests.patch(database_url + "control.json?auth=" + auth_token, data=ujson.dumps(dict3))
                            time.sleep(1)
                            request3.close()
                            time.sleep(1)
                        except Exception as e:
                            print("Error send waterpump2 data")
                            print(e)
                            time.sleep(1)
                    time.sleep(10)
                except Exception as e:
                    if not sta_if.isconnected():
                        print("ERROR, GET RECONNECTING WIFI")
                        connectWifi()
                    print("ERROR")
                    print(e)
                    time.sleep(3)  
                
        else:
            uart.write('Ready'.encode('utf-8'))
            time.sleep(2)
    except Exception as e:
        if not sta_if.isconnected():
            print("ERROR, RECONNECTING WIFI")
            connectWifi()
        print("ERROR")
        for i in range(NUM_LEDS):
            ar[i] = 255<<8          # shift 8 bits to the left
        sm.put(ar,8)
        print(e)
        time.sleep(3) 