# netlogo-queue
Netlogo extension providing a queue (with statistics).

------

November 2019: Updated to work with NetLogo 6.1! You can now easily install the queue extension via the NetLogo Extension Manager.

Please refer to the [documentation](https://github.com/ruthmore/netlogo-queue/blob/master/QueueDocumentation.pdf) for details. You may also have a look at the [example model](https://github.com/ruthmore/netlogo-queue/tree/master/examples) provided with this extension.

------

Queue is an extension for NetLogo, providing a commonly-used data type in discrete-event simulation. Objects can be inserted into a queue at a particular point in simulated time according to a specified queueing strategy (default is "first in , first out" (FIFO), but "last in, first out" (LIFO) is also supported). Requests to remove an element from a queue always returns the top-most object. 

Queues can be questioned as to their current status (length, empty?) and time-weighted statistics are calculated (mean length, max length, mean waiting time, max waiting time). Trying to insert an object with an associated simulation time lying in the past (i.e. a simulation time < last access time of the queue) throws an exception.

----
Extension developed under the DiDIY Project funded from the European Union's Horizon 2020 research and innovation programme under grant agreement No 644344. The views expressed here do not necessarily reflect the views of the EC.
