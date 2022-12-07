const admin = require("/opt/node_modules/firebase-admin");
const cert = {
  projectId: process.env.FIREBASE_PROJECT_ID,
  clientEmail: process.env.FIREBASE_CLIENT_EMAIL,
  privateKey: process.env.FIREBASE_PRIVATE_KEY.replace(/\\n/gm, "\n"),
};


admin.initializeApp({
  credential: admin.credential.cert(cert),
});


exports.handler = (event) => {
  const data =event.queryStringParameters;
  const msg = {
    notification: {
      title: data.title,
      body: data.body,
    },
    token: data.token
  };

  admin.messaging().send(msg).then(
    res => console.log({ res })
  ).catch(
    err => console.error({ err })
  );
}