###
import tensorflow as tf
import numpy as np
import pandas as pd
from tensorflow.contrib import rnn
import matplotlib.pyplot as plt
from datetime import datetime
from scipy.spatial.distance import pdist

# Data Dimension
num_input = 42         # MNIST data input (image shape: 28x28)
timesteps = 20        # Timesteps
n_classes = 4         # Number of classes, one class per digit


# Helper Functions to load data
def load_data(mode='train'):
    """
     Function to load training data
    :param mode:train or test
    :return data and the corresponding labels
    """
    l=pd.read_csv("D:\\xianglin\git_space\\bi-direct-rnn\\input\\test_label.csv")
    d=pd.read_csv("D:\\xianglin\git_space\\bi-direct-rnn\\input\\test.csv")
    
    la=l.values.tolist()    #200000*2
    da=d.values.tolist()    #200000*8 
    
    label=np.array(la)      #200000*2
    data=np.array(da)       #200000*8
    
    label=np.reshape(label,(10000,timesteps,n_classes))      #10000*30
    data=np.reshape(data,(10000,timesteps,num_input))       #10000*120
    
    if mode=='train':
        x_train=data[0:6000]
        y_train=label[0:6000]
        x_valid=data[6000:8000]
        y_valid=label[6000:8000]
        return x_train,y_train,x_valid,y_valid
    else:
        x_test=data[8000:]
        y_test=label[8000:]
        return x_test,y_test
    
    
def get_next_batch(x,y,start,end):
    x_batch=x[start:end]
    y_batch=y[start:end]
    return x_batch,y_batch

x_train,y_train,x_valid,y_valid=load_data(mode='train')
print("Size of:")
print("- Training-set:\t\t{}".format(len(x_train)))
print("- Validation-set:\t\t{}".format(len(x_valid)))

# Hyperparameters
learning_rate=0.001 # The opetimization initial learning rate
epoches=100          # Total number of training epoches
batch_size=10       # Training batch size
display_freq=100     # Frequency of displaying the training results

# Network configurations
num_hidden_units=128# Number of hidden units of the RNN

# Network Helper Functions

#weight and bais wrappers
def weight_variable(shape):
    """
    Create a weight variable with appropriate initialization
    :param name:weight name
    :param shape:weight shape
    :return: initialization weight variable
    """
    initer=tf.truncated_normal_initializer(stddev=0.01)
    return tf.get_variable('W',
                           dtype=tf.float32,
                           shape=shape,
                           initializer=initer)
    
def bias_variable(shape):
    """
    Create a bais variable with appropriate initialization
    :param name:bias name
    :param shape:bias shape
    :return: initialization bias variable
    """
    initial=tf.constant(0.,shape=shape,dtype=tf.float32)
    return tf.get_variable('b',dtype=tf.float32,initializer=initial)

# Building a RNN network
    
def BiRNN(x,weights,biases,timesteps,num_hidden):
    # Prepare data shape to match 'rnn' function requirement
    # Current data input shape: (batch_size,timesteps,n_inputs)
    # Required shape: 'timesteps' tensors list of shape (batch_size,num_input)
    
    # Unstack to get a list of 'timesteps' tensors of shape (batch_size, num_input)
    x = tf.unstack(x, timesteps, 1)

    # Define lstm cells with tensorflow
    # Forward direction cell
    lstm_fw_cell = rnn.BasicLSTMCell(num_hidden, forget_bias=1.0)
    # Backward direction cell
    lstm_bw_cell = rnn.BasicLSTMCell(num_hidden, forget_bias=1.0)

    # Get BiRNN cell output
    outputs, _, _ = rnn.static_bidirectional_rnn(lstm_fw_cell, lstm_bw_cell, x,
                                                 dtype=tf.float32)
    
    pred=[]
    
    # Linear activation, using rnn inner loop last output
    for output in outputs:
        pred.append(tf.matmul(output, weights) + biases)
        
    return pred

#create the network graph
    
# Placeholders for inputs (x) and outputs(y)
x = tf.placeholder(tf.float32, shape=[None, timesteps, num_input], name='X')

y = tf.placeholder(tf.float32, shape=[None, timesteps, n_classes], name='Y')

# create weight matrix initialized randomely from N~(0, 0.01)
W = weight_variable(shape=[2*num_hidden_units, n_classes])

# create bias vector initialized as zero
b = bias_variable(shape=[n_classes])

outputs = BiRNN(x, W, b, timesteps, num_hidden_units)

#outputs=np.array(output_logits)


#y_pred = tf.nn.softmax(output_logits)

# Model predictions
#cls_prediction = tf.argmax(output_logits, axis=1, name='predictions')
def HybridLoss(outputs,labels,weights,biases):
    y_pred=tf.concat(outputs,axis=0)
    y_labels=tf.concat(tf.unstack(labels, timesteps, 1),axis=0)
    # outputs:(list)    (?,4)
    # y:(list)          (?,4)
    y_pred_strategy = y_pred[0:, 0:3]
    y_pred_timeout = y_pred[0:, 3:]

    label_strategy = y_labels[0:, 0:3]
    label_timeout = y_labels[0:, 3:]

    loss1 = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits_v2(labels=label_strategy, logits=y_pred_strategy))+0.045*tf.nn.l2_loss(weights) + 0.045*tf.nn.l2_loss(biases)
    loss2 = tf.losses.mean_squared_error(label_timeout, y_pred_timeout)

    loss=loss1+loss2

    return loss

def cosine(q,a):
    normalize_q = tf.nn.l2_normalize(q,0)        
    normalize_a = tf.nn.l2_normalize(a,0)
    cos_similarity=tf.reduce_sum(tf.multiply(normalize_q,normalize_a))
    return cos_similarity


def Accuracy(outputs,labels):

    y_pred = tf.concat(outputs, axis=0)
    y_labels = tf.concat(tf.unstack(labels, timesteps, 1), axis=0)

    y_pred_strategy = y_pred[0:, 0:3]
    label_strategy = y_labels[0:, 0:3]

    accu = tf.equal(tf.argmax(y_pred, 1), tf.argmax(y_labels, 1), name='correct_pred')

    # normalize_y_pred = tf.nn.l2_normalize(y_pred, 0)
    # normalize_y_labels = tf.nn.l2_normalize(y_labels, 0)
    # accu=tf.losses.cosine_distance(normalize_y_labels, normalize_y_pred,axis=0)

    # accu=[]
    # for i in range(len(y_pred)):
    #     # accu.append((cosine(y_pred[i],y_labels[i])).eval(session=sess))
    #     i
    return accu
    

# Define the loss function, optimizer, and accuracy
#loss = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=y, logits=outputs), name='loss')
#loss = tf.losses.mean_squared_error(y_true, y_pred)
loss=HybridLoss(outputs,y,W,b)
optimizer = tf.train.AdamOptimizer(learning_rate=learning_rate, name='Adam-op').minimize(loss)
#correct_prediction = tf.equal(tf.argmax(outputs, 1), tf.argmax(y, 1), name='correct_pred')
correct_prediction=Accuracy(outputs,y)
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32), name='accuracy')

# Creating the op for initializing all variables
init = tf.global_variables_initializer()

sess = tf.InteractiveSession()
sess.run(init)
global_step = 0
# Number of training iterations in each epoch
num_tr_iter = int(len(y_train) / batch_size)
training_epoch=[]
validation_loss=[]
validation_accu=[]
training_loss=[]
training_accu=[]
for epoch in range(epoches):
    print('Training epoch: {}'.format(epoch + 1))
    iter_loss=[]
    iter_accu=[]
    for iteration in range(num_tr_iter):
        global_step += 1
        start = iteration * batch_size
        end = (iteration + 1) * batch_size
        x_batch, y_batch = get_next_batch(x_train, y_train, start, end)
        x_batch = np.reshape(x_batch,(batch_size, timesteps, num_input))
        y_batch = np.reshape(y_batch,(batch_size, timesteps, n_classes))
        # Run optimization op (backprop)
        feed_dict_batch = {x: x_batch, y: y_batch}
        sess.run(optimizer, feed_dict=feed_dict_batch)

        if iteration % display_freq == 0:
            # Calculate and display the batch loss and accuracy
            loss_batch, acc_batch = sess.run([loss, accuracy],
                                             feed_dict=feed_dict_batch)

            iter_loss.append(loss_batch)
            iter_accu.append(acc_batch)
            print("iter {0:3d}:\t Loss={1:.2f},\tTraining Accuracy={2:.01%}".
                  format(iteration, loss_batch, acc_batch))
    #count loss for training data
    iter_average_loss=sum(iter_loss)/len(iter_loss)
    iter_average_accu=sum(iter_accu)/len(iter_accu)
    training_loss.append(iter_average_loss)
    training_accu.append(iter_average_accu)

    # Run validation after every epoch

    feed_dict_valid = {x: x_valid[:2000].reshape((-1, timesteps, num_input)), y: y_valid[:2000].reshape((-1, timesteps, n_classes))}
    loss_valid, acc_valid = sess.run([loss, accuracy], feed_dict=feed_dict_valid)
    training_epoch.append(epoch+1)
    validation_loss.append(loss_valid)
    validation_accu.append(acc_valid)
    print('---------------------------------------------------------')
    print("Epoch: {0}, validation loss: {1:.2f}, validation accuracy: {2:.01%}".
          format(epoch + 1, loss_valid, acc_valid))
    print('---------------------------------------------------------')

# Test
x_test,y_test=load_data(mode='test')
feed_dict_test={x:x_test.reshape((-1,timesteps,num_input)),y:y_test.reshape((-1,timesteps,n_classes))}
loss_test,acc_test=sess.run([loss,accuracy],feed_dict=feed_dict_test)
print('---------------------------------------------------------')
print("Test loss: {0:.2f}, test accuracy: {1:.01%}".format(loss_test, acc_test))
print('---------------------------------------------------------')

#print result
DATETIME_NOW = datetime.now().strftime('%Y-%m-%d_%H-%M-%S')

#scatter plot
plt.plot(training_epoch,validation_loss, 'r--')
plt.plot(training_epoch,training_loss, 'b-')
plt.legend(['Validation Loss', 'Training Loss'])

#change axes ranges

#add title
plt.title('Relationship Between Epochs and Losses')

#add x and y labels
plt.xlabel('Epoch')
plt.ylabel('Loss')
plt.savefig('result_loss_{}.png'.format(DATETIME_NOW))

plt.cla()

plt.plot(training_epoch,validation_accu,'r--')
plt.plot(training_epoch,training_accu,'b-')
plt.legend(['Validation_accu','Training_accu'])
plt.title('Relationship between Epochs and Accuracy')
plt.xlabel('Epoch')
plt.ylabel('Accuracy')

plt.savefig('result_accuracy_{}.png'.format(DATETIME_NOW))










