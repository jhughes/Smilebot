#!/usr/bin/env ruby
require 'socket'
require 'io/wait'

def log(type, msg)
	puts "[#{type.to_s.capitalize}] #{msg}" if type == :calc
	puts "[#{type.to_s.capitalize}] #{msg}" if type == :sync
end

class PaintingClient 
  @AMPLITUDE = 75
  PERIOD = 500      # How many pixels before the wave repeats
  TWO_PI = Math::PI * 2 
  XSPACING = 8
  @XMAX = (640+16)/XSPACING
  DX = ( TWO_PI / PERIOD) * XSPACING

	# So the port gets opened, connects to the host
	# and then listens for the first sync message
	def initialize( host, port )

    # Initialize your variablez
    @theta = 0.0
    @x = 0.0
    @timeslot_length = 100
    @color = generate_color
    #@mutex = Mutex.new

		# Start the connection
		@sock = TCPSocket.new(host, port)
		@alive = true

    # Get the first broadcast
    recv_sync
	end

  def generate_color
    r = Random.rand(255)
    g = Random.rand(255)
    b = Random.rand(255)
    a = 255
    color = a << 24 | r << 16 | g << 8 | b
    return color
  end

	def start_sync_thread
		@sync_thread = Thread.new {
			while @alive do
				recv_sync
			end
		}
	end

  def start_paint_thread
    @paint_thread = Thread.new {
      puts "Starting paint thread"
      last_time  = Time.now
      while @alive do
        current_time = Time.now
        if( (current_time - last_time) * 1000 > @timeslot_length )
          last_time = current_time
          recv_sync
          calc_wave_and_send
        end
      end
    }
  end

	# Start sending packets to the server  
	# Reset the timer each time in case sync changes
  def calc_wave_and_send
    if @synced
      y = Math.sin((@x + @theta) * DX) * @AMPLITUDE
      col = @client_pos
      if @current_client == @client_pos
        log( :calc, "x:#{@x}\ty:#{y}\tcol:#{@color}")
        y_msg = [y].pack('f')
        color_msg = [@color].pack('l')
        #x_msg = [@x].pack('l')
        #message = y_msg + color_msg + x_msg
        message = y_msg + color_msg
        @sock.send(message, 0)
        @sock.flush
      end

      @x = (@x + 1)
      if @x % @XMAX == 0
        @theta += 0 # No angular velocity for now
        @x = 0
      end
      @current_client = (@current_client + 1) % @num_clients
    end
  end

	# Recieve the sync message from the server
	def recv_sync

    if @sock.ready?
      puts "Recieving sync"
      @num_clients = @sock.recvfrom(4)[0].unpack('N')[0]
      @client_pos = @sock.recvfrom(4)[0].unpack('N')[0]
      @current_client = @sock.recvfrom(4)[0].unpack('N')[0]
      @x = @sock.recvfrom(4)[0].unpack('N')[0]
      @XMAX = @sock.recvfrom(4)[0].unpack('N')[0]
      @AMPLITUDE = @sock.recvfrom(4)[0].unpack('N')[0] / 2
      @synced = true
      log( :sync, "num_clients:#{@num_clients}, client_pos:#{@client_pos}, @current_client:#{@current_client} x:#{@x}, XMAX:#{@XMAX}, AMPLITUDE:#{@AMPLITUDE}" )
    end
	end

	def stop
    @alive = false
		@sock.close
	end

  def start
    @alive = true
		#start_sync_thread
		start_paint_thread

    @paint_thread.join
    #@sync_thread.join
  end
	
end

p = PaintingClient.new( '192.168.1.5', 5000 )
begin
  p.start
rescue Exception => e
  p.stop
end
