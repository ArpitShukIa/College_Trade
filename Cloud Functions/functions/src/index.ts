import * as functions from 'firebase-functions'
import * as admin from 'firebase-admin'
admin.initializeApp()

const db = admin.firestore()

export const sendMessage = 
functions.region('asia-east2')
.firestore.document('Chats/{chatId}/Messages/{msgId}')
.onCreate(async (snapshot, context) => {
    const msg = snapshot.data()
    const chatId = context.params.chatId
    const chatRoomRef = db.doc(`Chats/${chatId}`)

    try {
        // Update message status to SENT
        await db.runTransaction(async (t) => {
            const doc = await t.get(snapshot.ref)
            const data = doc.data()
            if(data && data.status === 0) {
                t.update(snapshot.ref, {status: 1})
                t.update(chatRoomRef, {'lastMsg.status': 1})
            }
        })

        // Get device token
        let token_id = msg.deviceToken
        if(token_id === "") {
            const docRef = db.doc(`Users/${msg.to}`)
            const user = (await docRef.get()).data()
            if(user)
                token_id = user.deviceToken
        }

        // Send a data message to client's device
        const payload = {
            data: {
                id: msg.id,
                message: msg.message,
                name: msg.senderName,
                image: msg.senderImage,
                myImage: msg.receiverImage,
                sender: msg.from,
                receiver: msg.to,
                time: msg.timestamp,
                deviceToken: msg.deviceToken
            }
        }
        return admin.messaging().sendToDevice(token_id, payload)
    }
    catch(error) {
        console.error(error);
        return null
    }
})