import json
from features_extractor import FightingFeaturesExtractor

#Load the JSON log
with open('FileName.json', 'r') as f:
    data = json.load(f)

# Construct a new object by passing a list of the desired features (this is used later to retrieve the features in bulk).
fe = FightingFeaturesExtractor(['P1.attack.hit_area.bottom','players_distance()'])

# Inform the features extractor of the game data.
fe.set_game_data(data)

# Iterate through the rounds and the frames.
for round_data in data['rounds']:
    for frame_data in round_data:

        # Let's print only some random frames in this demo
        import random
        if random.random() > 0.0002:
            continue

        # Get HP
        print('P2 HP: %s' % fe.get_hp(frame_data, 'P2'))

        # Cache the frame data in the features extractor
        fe.set_frame_data(frame_data)

        # Get a feature: use a name with dotted notation and call the method get_feature().
        print('P1 state: : %s' % fe.get_feature('P1.state'))
        print('P2 remaining frames: %s' % fe.get_feature('P2.remaining_frames'))
        print('P2 hit-area bottom: %s' % fe.get_feature('P2.bottom'))
        print('P1 attack hit-area top: %s' % fe.get_feature('P1.attack.hit_area.top')) # Could be None if there is no attack
        print('P2 first projectile impact x: %s' % fe.get_feature('P2.projectiles[0].impact_x')) # Could be None if there are not projectiles

        # Get number of projectiles of P2
        print('P2 no. of projectiles: %s' % fe.get_feature('P2.projectiles.count'))
        if fe.get_feature('P2.projectiles.count') > 0:
            print('P2 first projectile hit-area left: %s' % fe.get_feature('P2.projectiles[0].hit_area.left'))


        # Get some special features (the second parameter passed to get_special indicates who is the "player")
        print('Distance between players hit-boxes: %s' % fe.get_special('players_x_distance()', 'P1'))
        print('Difference in X of players: %s' % fe.get_special('players_x_diff()', 'P1'))
        print('Difference in Y of players: %s' % fe.get_special('players_y_diff()', 'P1'))
        print('Sign of player y speed: %s' % fe.get_special('player_is_falling()', 'P1'))
        print('Sign of opponent y speed: %s' % fe.get_special('opponent_is_falling()', 'P1'))
        print('Is opponent getting closer: %s' % fe.get_special('opponent_is_approaching()', 'P1'))
        print('Is opponent attacking: %s' % fe.get_special('opponent_is_attacking()', 'P1'))
        print('Distance between the player hit-area and the hit-area of the closest opponent attack or projectile: %s' % fe.get_special('closest_threat_x_distance()', 'P1'))
        print('Distance between the player hit-area and the hit-area of the opponent attack: %s' % fe.get_special('attack_x_distance()', 'P1'))
        print('Distance between the player hit-area and the hit-area of the closest opponent projectile: %s ' % fe.get_special('closest_projectile_x_distance()', 'P1'))
        print('Can opponent perform action: %s' % fe.get_special('opponent_is_busy()', 'P1'))

        print()

print()

# Get information about features (can be useful when you want to normalize continuous features or convert enum features to one-hot representations, etc).
print('Info of feature "P1.state": %s' % fe.get_feature_info('P1.state'))
print('Info of feature "P1.action": %s' % fe.get_feature_info('P1.action'))
print('Info of feature "P2.remaining_frames": %s' % fe.get_feature_info('P2.remaining_frames'))
print('Info of feature "P2.projectiles[0].impact_x": %s' % fe.get_feature_info('P2.projectiles[0].impact_x'))
print('Info of feature "players_x_distance()": %s' % fe.get_feature_info('players_x_distance()'))
print('Info of feature "players_x_diff()": %s' % fe.get_feature_info('players_x_diff()'))
print('Info of feature "player_is_falling()": %s' % fe.get_feature_info('player_is_falling()'))
print('Info of feature "closest_threat_x_distance()": %s' % fe.get_feature_info('closest_threat_x_distance()'))
print('Info of feature "attack_x_distance()": %s' % fe.get_feature_info('attack_x_distance()'))
print('Info of feature "closest_projectile_x_distance()": %s' % fe.get_feature_info('closest_projectile_x_distance()'))



# --- OUTPUT ---

# P2 HP: 480
# P1 state: : CROUCH
# P2 remaining frames: 42
# P2 hit-area bottom: 640
# P1 attack hit-area top: None
# P2 first projectile impact x: None
# P2 no. of projectiles: 0
# Distance between players hit-boxes: 2
# Difference in X of players: 3
# Difference in Y of players: 0
# Sign of player y speed: 0
# Sign of opponent y speed: 0
# Is opponent getting closer: False
# Is opponent attacking: False
# Distance between the player hit-area and the hit-area of the closest opponent attack or projectile: None
# Distance between the player hit-area and the hit-area of the opponent attack: None
# Distance between the player hit-area and the hit-area of the closest opponent projectile: None
# Can opponent perform action: True
#
# P2 HP: 64
# P1 state: : AIR
# P2 remaining frames: 32
# P2 hit-area bottom: 640
# P1 attack hit-area top: 297
# P2 first projectile impact x: 10
# P2 no. of projectiles: 1
# P2 first projectile hit-area left: 624
# Distance between players hit-boxes: 2
# Difference in X of players: 3
# Difference in Y of players: -3
# Sign of player y speed: 1
# Sign of opponent y speed: 0
# Is opponent getting closer: False
# Is opponent attacking: False
# Distance between the player hit-area and the hit-area of the closest opponent attack or projectile: None
# Distance between the player hit-area and the hit-area of the opponent attack: None
# Distance between the player hit-area and the hit-area of the closest opponent projectile: None
# Can opponent perform action: True
#
#
# Info of feature "P1.state": {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': ['STAND', 'CROUCH', 'AIR', 'DOWN']}
# Info of feature "P1.action": {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': ['NEUTRAL', 'STAND', 'FORWARD_WALK', 'DASH', 'BACK_STEP', 'CROUCH', 'JUMP', 'FOR_JUMP', 'BACK_JUMP', 'AIR', 'STAND_GUARD', 'CROUCH_GUARD', 'AIR_GUARD', 'STAND_GUARD_RECOV', 'CROUCH_GUARD_RECOV', 'AIR_GUARD_RECOV', 'STAND_RECOV', 'CROUCH_RECOV', 'AIR_RECOV', 'CHANGE_DOWN', 'DOWN', 'RISE', 'LANDING', 'THROW_A', 'THROW_B', 'THROW_HIT', 'THROW_SUFFER', 'STAND_A', 'STAND_B', 'CROUCH_A', 'CROUCH_B', 'AIR_A', 'AIR_B', 'AIR_DA', 'AIR_DB', 'STAND_FA', 'STAND_FB', 'CROUCH_FA', 'CROUCH_FB', 'AIR_FA', 'AIR_FB', 'AIR_UA', 'AIR_UB', 'STAND_D_DF_FA', 'STAND_D_DF_FB', 'STAND_F_D_DFA', 'STAND_F_D_DFB', 'STAND_D_DB_BA', 'STAND_D_DB_BB', 'AIR_D_DF_FA', 'AIR_D_DF_FB', 'AIR_F_D_DFA', 'AIR_F_D_DFB', 'AIR_D_DB_BA', 'AIR_D_DB_BB', 'STAND_D_DF_FC']}
# Info of feature "P2.remaining_frames": {'iterable': False, 'nullable': False, 'type': <class 'int'>, 'min': 0, 'max': 3615}
# Info of feature "P2.projectiles[0].impact_x": {'iterable': False, 'nullable': False, 'type': <class 'int'>, 'min': 0, 'max': 30}
# Info of feature "players_x_distance()": {'iterable': False, 'nullable': False, 'type': <class 'int'>, 'min': 0, 'max': 960}
# Info of feature "players_x_diff()": {'iterable': False, 'nullable': False, 'type': <class 'int'>, 'min': 0, 'max': 960}
# Info of feature "player_is_falling()": {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': [-1, 0, 1]}
# Info of feature "closest_threat_x_distance()": {'iterable': False, 'nullable': True, 'type': <class 'int'>, 'min': 0, 'max': 960}
# Info of feature "attack_x_distance()": {'iterable': False, 'nullable': True, 'type': <class 'int'>, 'min': 0, 'max': 960}
# Info of feature "closest_projectile_x_distance()": {'iterable': False, 'nullable': True, 'type': <class 'int'>, 'min': 0, 'max': 960}

