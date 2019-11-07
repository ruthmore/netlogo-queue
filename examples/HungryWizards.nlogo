;; ----------------------------------------------------------------------------------
;; HungryWizards: an example model for the queue extension.
;;
;; Commands related to the queue extension are marked with <----- in the comments.
;; The plot and the two monitors also use queue primitives: queue:length, queue:mean-wt
;; and queue:mean-length, respectively.
;;
;; Author: Ruth Meyer
;; ----------------------------------------------------------------------------------

extensions [ queue ]          ;; <----- import the queue extension

globals [
  the-q                       ;; this global variable holds the queue
]

breed [honeydukes honeyduke]  ;; the sweet shop
breed [wizards wizard]        ;; the (hungry) wizards

honeydukes-own [
  is-open?
  next-opening
  next-closing
]

wizards-own [
  is-waiting?
  is-hungry?
  is-eating-for
  is-digesting-for
]

;; initialise the model
to setup
  clear-all

  set the-q queue:create 0    ;; <------ create the queue with FIFO strategy

  ;; make a chequerboard pattern
  ask patches [ set pcolor grey ]
  ask patches with [is-odd? pxcor and is-odd? pycor] [ set pcolor 4 ]
  ask patches with [is-even? pxcor and is-even? pycor] [ set pcolor 4 ]

  ;; create the wizards
  let w-colors remove grey base-colors
  create-wizards num-wizards [
    set is-hungry? true
    set is-waiting? false
    set is-eating-for 0
    set shape "face sad"
    set size 1
    set color item (who mod length w-colors) w-colors
    move-to one-of patches with [not any? turtles-here]
  ]

  ;; create the sweet shop
  create-honeydukes 1 [
    set is-open? false
    set next-opening 5
    set next-closing -1
    set shape "house"
    set size 1
    set color blue
    move-to one-of patches with [not any? turtles-here]
  ]

  reset-ticks
  talk "----- starting model"
end

to go
  ;; let the shop do something
  ask honeydukes [
    update-h
  ]
  ;; let the wizards do something
  ask wizards [
    update-w
  ]

  output-print queue:show the-q    ;; <------ show the contents of the queue

  tick
end

;; wizard moves ---------------------------------------------------------

to update-w
  if (is-hungry? and not is-waiting?) [ find-Honeydukes ]
  if (is-eating-for > 0) [ eat ]
  if (is-digesting-for > 0) [ digest ]
end

to find-Honeydukes
  talk "is looking for Honeydukes"
  let hd honeydukes-on neighbors
  ifelse (any? hd) [
    face one-of hd
    fd 1
    enter-queue

  ][
    ;; move randomly
    move-to one-of neighbors
  ]
end

to enter-queue
  talk "is entering the queue"
  set is-waiting? true
  queue:insert the-q self ticks       ;; <------ enter the queue
end

to start-on-sweets
  talk "is starting on sweets"
  set is-waiting? false
  ;; move to a random spot and eat
  move-to one-of patches with [not any? turtles-here]
  set is-hungry? false
  set shape "face happy"
  set is-eating-for 2 + random 10
end

to eat
  talk "is eating"
  set is-eating-for is-eating-for - 1
  if (is-eating-for = 0) [
    set is-digesting-for 5 + random 10
    set shape "face neutral"
  ]
end

to digest
  talk "is digesting"
  set is-digesting-for is-digesting-for - 1
  if (is-digesting-for = 0) [
    set is-hungry? true
    set shape "face sad"
  ]
end

;; honeydukes moves --------------------------------------------------

to update-h
  open-shop
  if (is-open?) [ serve-customer ]
  close-shop
end

to serve-customer
  if (not queue:empty? the-q) [
    let customer queue:remove the-q ticks         ;; <----- remove first element of the queue
    talk (word "is serving customer " customer)
    ask customer [ start-on-sweets ]
  ]
end

to close-shop
  if (next-closing = ticks) [
    set is-open? false
    set next-opening ticks + 12
    talk "is closing shop"
    set color blue
  ]
end

to open-shop
  if (next-opening = ticks) [
    set is-open? true
    set next-closing ticks + 12
    talk "is opening shop"
    set color yellow
  ]
end

;; general stuff --------------------------------------------------------

to talk [stuff]
  if (let-them-talk?) [ show (word stuff " at time " ticks)]
end

to-report is-odd? [n]
  report n mod 2 > 0
end

to-report is-even? [n]
  report n mod 2 = 0
end

@#$#@#$#@
GRAPHICS-WINDOW
210
10
414
215
-1
-1
28.0
1
10
1
1
1
0
1
1
1
-3
3
-3
3
0
0
1
ticks
30.0

BUTTON
42
22
108
55
NIL
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
42
70
105
103
step
go
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
116
70
179
103
NIL
go
T
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

SWITCH
39
165
183
198
let-them-talk?
let-them-talk?
0
1
-1000

PLOT
458
20
658
170
queue length
NIL
NIL
0.0
10.0
0.0
4.0
true
false
"" ""
PENS
"default" 1.0 0 -16777216 true "" "plot queue:length the-q"

MONITOR
460
181
553
226
avg wait time
queue:mean-wt the-q
3
1
11

MONITOR
567
181
658
226
avg length
queue:mean-length the-q
3
1
11

SLIDER
39
118
184
151
num-wizards
num-wizards
1
20
15.0
1
1
NIL
HORIZONTAL

OUTPUT
211
235
660
322
13

TEXTBOX
214
220
364
238
The queue:
11
0.0
1

@#$#@#$#@
## WHAT IS IT?

This is a silly little example model to show the use of the queue extension without implementing a full discrete-event simulation. 

The model consists of a bunch of wizards and a sweet shop ("Honeydukes"). Wizards are represented by faces with different expressions: sad, when the wizard is hungry; happy, when the wizard is eating sweets; and neutral, when the wizard is digesting the sweets. The shop is represented by a house in different colours: yellow, when open; or blue, when closed. The shop has a queue that all customers have to enter before they can buy any sweets. 

## HOW IT WORKS

When wizards are hungry (sad face), they move around randomly looking for the sweet shop. Once they have found it, they enter the queue and wait to be served. When a wizard has acquired some sweets, they apparate to a free spot and start eating (happy face). Once they've finished the sweets they digest them for a while (neutral face) before they turn hungry again and the search for the shop starts again.

The shop opens at tick 5 and then alternates between being open (yellow) and being closed (blue), each for twelve ticks. When open, the shop serves the customers waiting in the queue one at a time. Note that the shop "re-activates" the waiting wizard by asking her/him to perform their _start-on-sweets_ procedure. This is a common feature of queueing systems.

## HOW TO USE IT

To initialise the model click on the _setup_ button, which creates wizards and the shop in random locations. The number of wizards can be adjusted between 1 and 20 by using the slider _num-wizards_. 

To run the model click either the _step_ button (one tick) or the _go_ button (endless loop). 

## THINGS TO NOTICE

On the right-hand side there is a plot of the current length of the queue. Underneath that you can see the average wait time and average length displayed. Underneath those there is an output area showing the current contents of the queue. Note that each element (a wizard) is marked with the time of its entry.

With the _let-them-talk?_ switch you can toggle model output to the command center. If the switch is on, the wizards and the shop tell you what they are up to. This slows down model execution so if you switch it off and click _go_ the model will run very fast.

I would suggest to start exploring the model by using the _step_ button a few times so you can see what is happening.

## THINGS TO TRY

Try out different numbers of wizards.

## EXTENDING THE MODEL

At the moment, the wizards have no memory. As they can't remember the location of the sweet shop they have to search for it again and again whenever they become hungry. Can you change this behaviour so that once they have found the shop for the first time they are able to remember where it is and then walk towards it straight away when they are hungry? Does it have any impact on the queue?

## NETLOGO FEATURES

This model uses the queue extension.

## RELATED MODELS


## CREDITS AND REFERENCES

This model is loosely inspired by my obsession with Harry Potter. [Honeydukes](https://harrypotter.fandom.com/wiki/Honeydukes) is the name of the sweet shop in Hogsmeade, the village next to Hogwarts.
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

sheep
false
15
Circle -1 true true 203 65 88
Circle -1 true true 70 65 162
Circle -1 true true 150 105 120
Polygon -7500403 true false 218 120 240 165 255 165 278 120
Circle -7500403 true false 214 72 67
Rectangle -1 true true 164 223 179 298
Polygon -1 true true 45 285 30 285 30 240 15 195 45 210
Circle -1 true true 3 83 150
Rectangle -1 true true 65 221 80 296
Polygon -1 true true 195 285 210 285 210 240 240 210 195 210
Polygon -7500403 true false 276 85 285 105 302 99 294 83
Polygon -7500403 true false 219 85 210 105 193 99 201 83

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

wolf
false
0
Polygon -16777216 true false 253 133 245 131 245 133
Polygon -7500403 true true 2 194 13 197 30 191 38 193 38 205 20 226 20 257 27 265 38 266 40 260 31 253 31 230 60 206 68 198 75 209 66 228 65 243 82 261 84 268 100 267 103 261 77 239 79 231 100 207 98 196 119 201 143 202 160 195 166 210 172 213 173 238 167 251 160 248 154 265 169 264 178 247 186 240 198 260 200 271 217 271 219 262 207 258 195 230 192 198 210 184 227 164 242 144 259 145 284 151 277 141 293 140 299 134 297 127 273 119 270 105
Polygon -7500403 true true -1 195 14 180 36 166 40 153 53 140 82 131 134 133 159 126 188 115 227 108 236 102 238 98 268 86 269 92 281 87 269 103 269 113

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270
@#$#@#$#@
NetLogo 6.1.1
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180
@#$#@#$#@
0
@#$#@#$#@
