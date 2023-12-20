El microservicio se hizo con una arquitectura similar a la hexagonal.

Tiene un dominio, que lo hice sin lógica, solo con las clases de dominio.

Luego tiene una capa de aplicación, donde están los casos de uso en la clase OfferService.java que son:

- Consultar todas las ofertas: Función getOffers.

- Consultar ofertas utilizando filtros: Función getOffers(Integer importance, Integer urgency, String category)

- Elegir ofertas. Donde se pasa por parámetro el usuario correspondiente, con el método de pago, y la lista de ofertas que seleccionó.
	public List<Offer> getOffers(Integer importance, Integer urgency, String category)

Se me podría llegar preguntar por qué no puse esta lógica en el dominio. Podría ir en un service en el dominio, pero como hay mucho orquestado con otros microservicios, preferí ponerlo como parte de la capa de aplicación. En teoría el dominio debería contener sólo lógica, sin llamados hacia las interfaces de la infraestructura. Como vi que hay mucho uso de varios micros cuyos llamados se hacen en la infraestructura lo codié en la capa de aplicación. De todas formas podría ponerse un service en el dominio también.

En varios de los proyectos de mi trabajo, pongo en la capa de aplicación los controllers... y en el dominio hago todo el orquestado de interfaces de infra, pero eso es otro tipo de arquitectura que uso. Esto igual depende mucho de cuál es la forma de trabajar del área, y si se sienten cómodos armando micros de esta forma. La realidad es que solo pude codear el domingo...  en la semana no tengo mucho tiempo para codear por mi trabajo. La versatilidad que me da esta arquitectura que usé... es que si veo que no entendí bien algo, al tener desacoplados los puertos de la lógica principal, puedo modificar la misma con solo cambiar un puerto.

En la capa de infraestructura se podría decir que hay varios puertos:
	- puerto de clientes (ClientRepository): Es el microservicio al que estaría pidiendole que valide los datos del cliente.
	- puerto de Pagos (paymentsRepository): Es el microservicio al que estaría pidiendole que valide el método de pago.
	- puerto de Base de datos (offersRepository): Es la base de datos que usamos.
	
	Después tengo tres puertos donde codifico los mensajes que se enviarán por Kafka. En el enunciado se puso que había que notificar a Plataformas de Decisiones y a Entidades Bancarias las ofertas seleccionadas por el usuario. La palabra "notificar" en el enunciado lo tomé como que tenía que usarse Kafka o algún sistema de mensajería similar (RabbitMq es otro).
	
	- puerto de Plataforma de decisiones (decisionPlatformRepository). 
	- puerto de Entidades Bancarias (bankingEntitiesRepository).
	- puerto de Emails.
	
	
Cuando digo puerto me refiero a los packages donde estaría representada cada una de las entidades (clientes, pagos, base de datos, plataforma de decisiones, entidades bancarias e emails). En muchos lugares a estos se les en el nombre del package ".port.clients" por ejemplo, con los adapters y los mappers. En mi caso prefiero nombrarlos de una forma más sencilla y más corta.

Ahora supongamos que ustedes me dicen que me equivoqué y que la idea era esperar a que "Plataforma de decisiones" me responda, puedo modificar el puerto de plataforma de decisiones para que sea un llamado por POST directamente y modificar un poco la lógica para que actúe a partir de esa respuesta.

![image](https://github.com/emanuelmachado1983/offers/assets/33380573/ee93b11b-b822-430d-8a21-662abd36a754)


Cada vez que valido el usuario o el método de pago, y el micro destino da error, lo que hice fue guardar en una base de todas formas el pedido de ofertas. Para que un job vuelva a intentar la operación cada media hora, hasta un límite de dos horas.



Los test unitarios los hice solo para la capa de aplicación. Hice unos test sencillos con estilo de escuela de London que es el más usado en en el área en que trabajo. La idea de este tipo de tests, es testear de forma unitaria cada una de las clases.



No hice POST, ni PATCH, ni PUT de ofertas porque no tuve tiempo, igual creo que no era la idea del ejercicio. Por lo que entiendo las ofertas deberían poderse cargar con flyway por ejemplo. 
La forma que yo creo que deberían traerse las ofertas a este micro es... que el micro que hice se suscriba a un tópico de Kafka de donde toma las novedades de ofertas que se van a enviar de otro micro.




Bueno, por último, no pude hacerles un deploy. Lo cual entiendo que es determinante... sin embargo, actualmente no dispongo del tiempo necesario para llevar a cabo esta tarea, y mi conocimiento en Docker no es suficiente para configurar los servicios satélite requeridos.
Así que realicé el desarrollo con los test unitarios, y con una explicación amplia como para que puedan ver cómo lo plantié.









	








- Oferta por tiempo limitado
	importancia
	duracion
	urgencia
	categoria
	
Debería haber otro micro que se encargue de estas categorías con sus correspondientes post put get.
Y que cuando se haga un post o un put sobre ese micro se le envien las novedades al micro de ofertas (el que hice)
Estas novedades pueden ser por ejemplo usando kafka.


Selección de oferta.
- Validación de cliente. La validación de cliente debería hacerse en un servicio a parte. Que valide el cliente. No en este.
La idea es que en cada operación se envíe un token de autenticación para enviar al cliente correspondiente.

- Validación de medios de pago. Esto también se tiene que hacer en un servicio a parte. Se envía el mismo token de autenticación del cliente, y valida si no se puede.

- Almacenamiento de una instancia de una oferta al cliente. Se hace en base de datos, se guarda y después debería haber un job o un crobjob que elimine las ofertas viejas.
La idea es la siguiente...


1) Si dan OK las validaciones, devuelvo 200 (OK), para que el front le diga al cliente que su selección de oferta fue enviada al banco, y que este va a analizar si puede aplicar a las mismas.

2) Si dio 500 la validación del cliente. Lo guarde en mi tabla de almacenamiento de instancias, pero guardando que dio error la validación de cliente porque está abajo el servicio validador. Al cliente le aviso que por el momento no le puedo decir si está OK o no, pero que le voy a mandar un mail durante las siguientes dos horas, avisándole si está OK.

3) Si dio 500, voy a reintentar la operación cada media hora hasta un limité de dos horas. Si da OK, le mando un mensaje al cliente diciendole lo mismo que puse ne el punto 1  y sino le mando un mensaje diciendole que sus datos no están correctos. Si al cabo de dos horas sigue dandole error, le mando un mensaje al cliente de que su oferta no pudo ser aprobada por un error del sistema y que intente de nuevo.


4) Con la validación del medio de pago... idem al punto anterior.



Para lo que son notificaciones utilizo Kafka, aunque podría hacer algo parecido para los puntos anteriores, pasa que todo lo que ver con decisiones basadas en el perfil de un usuario (por ejemplo cuando vas a pedir un préstamo) tardan mucho. Por eso entiendo que como dice en el documento, es una notificación. El servidor de kafka debería intentar notificar cada 15 minutos hasta un rango de dos horas, o hasta una cierta cantidad de reintentos, y si ya no puede... va a dejar un informe de lo que no pudo hacer. Lo que asumo es que mi microservicio solo se encarga de hacer algunas validaciones y luego enviar las notificaciones... en caso de que el cliente sea aceptado, entiendo que informarlo sobre esto es obligación de los otros microservicios.

En caso de que haya asumido mal, habría que modificar el código para que las notificaciones funcionen de la misma forma que los incisos anteriores, es decir que no se le del ok al cliente hasta que se hayan notificado correctamente a todos lados. En este caso habría que hace un mecanismo de rollback entre notificaciones. O sea si vos primero notificaste a la plataforma de toma de decisiones si la oferta salió y a las entidades bancarias que también ya salió, si uno de esos servicios te dice que al cliente NO hay que darle la oferta, hay que avisarle al otro de la cancelación de la oferta por ejemplo.



Aclaraciones: 
La duración de la oferta en minutos, entendí que es la cantidad de minutos que va a tener el cliente para elegirla. Que de eso se va a encargar el widget... de mostrar.
Si el usuario, se pasó de esa cantidad de minutos, debería haber un bff que valide eso. O en todo caso que lo revisen en las notificaciones. O sea... yo nada más me estaría ocupando de enviar los datos de la oferta.

En la entidad en la que guardo la oferta seleccionada yo voy a tener el id de la oferta, con un código de usuario. Tanto en el banco Hipotecario, como en el Santander los usuarios que manejé, siempre tuvieron un código propio. Así que asumo que el usuario va a tener un código propio. Este código podría ser el CUIL por ejemplo.


A la hora de validar el cliente, asumo que la api que valida el cliente me devuelve un codigo con un valor "YES" si valida, y "NO" si no valida.

Lo mismo para la validación de pago, asumo que la api que valida si medio de pago de cliente devuelve en su response un código que puede ser "YES" or "NO"

