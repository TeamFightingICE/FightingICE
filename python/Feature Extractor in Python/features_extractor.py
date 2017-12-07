from typing import List, Optional, Tuple, Dict
import re
from functools import lru_cache
from action import ALL_ACTIONS

class FightingFeaturesExtractor:
    """
    Extracts Fighting ICE features.
    Features can be:
      - dot-separated strings, in which case they will be looked up recursively (e.g., 'P1.attack.hit_area.bottom')
      - special strings that compute functions over the available data (e.g., 'players_distance()')
    """

    def __init__(self, features: List[str]):
        self.features = features
        self.max_hp = None # type: Optional[Tuple[int, int]]
        self.character_names = None # type: Optional[Tuple[str, str]]
        self.stage_size = None # type: Optional[Dict[str, int]]
        self.frame_data = None
        self.discretize = True

    def set_game_data(self, game_data):
        if isinstance(game_data, dict):
            self.max_hp = game_data['max_hp']
            self.character_names = game_data['character_names']
            self.stage_size = game_data['stage_size']
        else:
            # max_hp info not available in game_data - will grab it from the first frameData
            self.character_names = {'P1': game_data.getPlayerOneCharacterName(), 'P2': game_data.getPlayerTwoCharacterName()}
            self.stage_size = {'x': game_data.getStageXMax(), 'y': game_data.getStageYMax()}

    @staticmethod
    def get_hp(frame_data, player):
        """
        Returns a given player's HP.
        :param frame_data: Frame data (can be a recursive dict or a java stub to a FrameData instance)
        :param player: Identifier for the player ('P1' or 'P2')
        :return: The given player's HP.
        """
        if isinstance(frame_data, dict):
            return frame_data[player]['hp']
        else:
            pl_data = frame_data.getP1() if player == 'P1' else frame_data.getP2()
            return FightingFeaturesExtractor.get_player_field(pl_data, 'hp')

    @staticmethod
    def get_action(frame_data, player):
        """
        Returns a given player's last action.
        :param frame_data: Frame data (can be a recursive dict or a java stub to a FrameData instance)
        :param player: Identifier for the player ('P1' or 'P2')
        :return: The given player's last action.
        """
        if isinstance(frame_data, dict):
            return frame_data[player]['action']
        else:
            pl_data = frame_data.getP1() if player == 'P1' else frame_data.getP2()
            return FightingFeaturesExtractor.get_player_field(pl_data, 'action')

    @staticmethod
    def get_energy(frame_data, player):
        """
        Returns a given player's energy.
        :param frame_data: Frame data (can be a recursive dict or a java stub to a FrameData instance)
        :param player: Identifier for the player ('P1' or 'P2')
        :return: The given player's energy.
        """
        if isinstance(frame_data, dict):
            return frame_data[player]['energy']
        else:
            pl_data = frame_data.getP1() if player == 'P1' else frame_data.getP2()
            return FightingFeaturesExtractor.get_player_field(pl_data, 'energy')

    @staticmethod
    def get_state(frame_data, player):
        """
        Returns a given player's state.
        :param frame_data: Frame data (can be a recursive dict or a java stub to a FrameData instance)
        :param player: Identifier for the player ('P1' or 'P2')
        :return: The given player's state.
        """
        if isinstance(frame_data, dict):
            return frame_data[player]['state']
        else:
            pl_data = frame_data.getP1() if player == 'P1' else frame_data.getP2()
            return FightingFeaturesExtractor.get_player_field(pl_data, 'state')

    @staticmethod
    def get_player_field(pl_data, field):
        if field == 'remaining_frames': return pl_data.getRemainingFrame()
        if field == 'action': return pl_data.getAction().toString()
        if field == 'action_id': return pl_data.getAction().ordinal()
        if field == 'state': return pl_data.getAction().toString()
        if field == 'state_id': return pl_data.getState().ordinal()
        if field == 'hp': return pl_data.getHp()
        if field == 'energy': return pl_data.getEnergy()
        if field == 'x': return pl_data.getX()
        if field == 'y': return pl_data.getY()
        if field == 'speed_x': return pl_data.getSpeedX()
        if field == 'speed_y': return pl_data.getSpeedY()
        if field == 'left': return pl_data.getLeft()
        if field == 'right': return pl_data.getRight()
        if field == 'top': return pl_data.getTop()
        if field == 'bottom': return pl_data.getBottom()
        raise ValueError("Unknown player field: %s" % field)

    @staticmethod
    def get_attack_field(att_data, field):
        if field == 'speed_x': return att_data.getSpeedX()
        if field == 'speed_y': return att_data.getSpeedY()
        if field == 'hit_damage': return att_data.getHitDamage()
        if field == 'guard_damage': return att_data.getGuardDamage()
        if field == 'start_add_energy': return att_data.getStartAddEnergy()
        if field == 'hit_add_energy': return att_data.getHitAddEnergy()
        if field == 'guard_add_energy': return att_data.getGuardAddEnergy()
        if field == 'give_energy': return att_data.getGiveEnergy()
        if field == 'give_guard_recov': return att_data.getGiveGuardRecov()
        if field == 'attack_type': return {1: 'HIGH', 2: 'MIDDLE', 3: 'LOW', 4: 'THROW'}[att_data.getAttackType()]
        if field == 'attack_type_id': return att_data.getAttackType()
        if field == 'impact_x': return att_data.getImpactX()
        if field == 'impact_y': return att_data.getImpactY()
        raise ValueError("Unknown attack field: %s" % field)

    @staticmethod
    def get_hit_area_field(hit_area_data, field):
        if field == 'top': return hit_area_data.getT()
        if field == 'bottom': return hit_area_data.getB()
        if field == 'left': return hit_area_data.getL()
        if field == 'right': return hit_area_data.getR()
        raise ValueError("Unknown hit area field: %s" % field)

    @lru_cache(maxsize=None)
    def get_feature(self, feature):
        fd = self.frame_data # shorthand

        if self.max_hp is None and not isinstance(fd, dict):
            self.max_hp = {'P1': fd.getP1().getMaxHp(), 'P2': fd.getP2().getMaxHp()}

        # feature of a player
        match = re.match(
            r'^(P1|P2).(remaining_frames|action|action_id|state|state_id|hp|energy|x|y|speed_x|speed_y|left|right|top|bottom)$',
            feature
        )
        if match:
            player = match.group(1)
            field = match.group(2)
            
            if isinstance(fd, dict):
                return fd[player][field]
            else:
                pl_data = fd.getP1() if player == 'P1' else fd.getP2()
                return FightingFeaturesExtractor.get_player_field(pl_data, field)

        # feature of an attack
        match = re.match(
            r'^(P1|P2).attack.(speed_x|speed_y|hit_damage|guard_damage|start_add_energy|hit_add_energy|'
            r'guard_add_energy|give_energy|give_guard_recov|attack_type|attack_type_id|impact_x|impact_y)$',
            feature
        )
        if match:
            player = match.group(1)
            field = match.group(2)
            if isinstance(fd, dict):
                if 'attack' in fd[player]:
                    return fd[player]['attack'][field]
                else:
                    return None
            else:
                pl_data = fd.getP1() if player == 'P1' else fd.getP2()
                att_data = pl_data.getAttack()
                return FightingFeaturesExtractor.get_attack_field(att_data, field)

        # feature of an attack hit area
        match = re.match(
            r'^(P1|P2).attack.hit_area.(bottom|top|left|right)$',
            feature
        )
        if match:
            player = match.group(1)
            field = match.group(2)
            if isinstance(fd, dict):
                if 'attack' in fd[player]:
                    return fd[player]['attack']['hit_area'][field]
                else:
                    return None
            else:
                pl_data = fd.getP1() if player == 'P1' else fd.getP2()
                att_data = pl_data.getAttack()
                hit_area_data = att_data.getHitAreaNow()
                return FightingFeaturesExtractor.get_hit_area_field(hit_area_data, field)

        # a player's projectiles count
        match = re.match(
            r'^(P1|P2).projectiles.count$',
            feature
        )
        if match:
            player = match.group(1)
            if isinstance(fd, dict):
                return len(fd[player]['projectiles'])
            else:
                projectiles = fd.getProjectilesByP1() if player == 'P2' else fd.getProjectilesByP2()
                return len(projectiles)

        match = re.match(
            r'^(P1|P2).projectiles\[([0-9]+)\].(speed_x|speed_y|hit_damage|guard_damage|start_add_energy|'
            r'hit_add_energy|guard_add_energy|give_energy|give_guard_recov|attack_type|attack_type_id|'
            r'impact_x|impact_y)$',
            feature
        )
        if match:
            player = match.group(1)
            projectile_index = int(match.group(2))
            field = match.group(3)
            if isinstance(fd, dict):
                if projectile_index < len(fd[player]['projectiles']):
                    return fd[player]['projectiles'][projectile_index][field]
                else:
                    return None
            else:
                projectiles = fd.getProjectilesByP1() if player == 'P2' else fd.getProjectilesByP2()
                return FightingFeaturesExtractor.get_attack_field(projectiles[projectile_index], field)

        match = re.match(
            r'^(P1|P2).projectiles\[([0-9]+)\].hit_area.(bottom|top|left|right)$',
            feature
        )
        if match:
            player = match.group(1)
            projectile_index = int(match.group(2))
            field = match.group(3)
            if isinstance(fd, dict):
                return fd[player]['projectiles'][projectile_index]['hit_area'][field]
            else:
                projectiles = fd.getProjectilesByP1() if player == 'P2' else fd.getProjectilesByP2()
                return FightingFeaturesExtractor.get_hit_area_field(projectiles[projectile_index].getHitAreaNow(), field)

        raise ValueError("Unknown feature: %s" % feature)



    def get_special(self, special, player):

        opponent = 'P2' if player == 'P1' else 'P1'

        if special == 'players_x_distance()':
            player_l = self.get_feature('%s.left' % player)
            player_r = self.get_feature('%s.right' % player)
            opponent_l = self.get_feature('%s.left' % opponent)
            opponent_r = self.get_feature('%s.right' % opponent)

            if player_l > opponent_r or player_r < opponent_l:
                value = min(abs(player_r - opponent_l), abs(player_l - opponent_r))
                if value == 0:
                    return None  # bounding boxes have overlapping y coordinates (map to special value None)
                else:
                    return discretize_intervals(value, thresholds=[100, 300]) if self.discretize else value
            else:
                return None # bounding boxes have overlapping x coordinates (map to special value None)

        elif special == 'players_x_diff()':
            player_x = self.get_feature('%s.x' % player)
            opponent_x = self.get_feature('%s.x' % opponent)
            value = abs(player_x - opponent_x)
            return discretize_intervals(value, thresholds=[90, 150, 300]) if self.discretize else value

        elif special == 'players_y_diff()':
            player_y = self.get_feature('%s.y' % player)
            opponent_y = self.get_feature('%s.y' % opponent)
            value = player_y - opponent_y
            if value >= 0:
                return discretize_intervals(value, thresholds=[30, 90, 150]) if self.discretize else value
            else:
                return -discretize_intervals(-value, thresholds=[30, 90, 150]) if self.discretize else value

        elif special == 'players_y_distance()':
            player_t = self.get_feature('%s.top' % player)
            player_b = self.get_feature('%s.bottom' % player)

            opponent_t = self.get_feature('%s.top' % opponent)
            opponent_b = self.get_feature('%s.bottom' % opponent)

            player_above_by = opponent_t - player_b
            opponent_above_by = player_t - opponent_b

            if player_above_by > 0:
                return discretize_intervals(player_above_by, thresholds=[150]) if self.discretize else player_above_by
            elif opponent_above_by > 0:
                return discretize_intervals(-opponent_above_by, thresholds=[150]) if self.discretize else -opponent_above_by
            else:
                return None  # bounding boxes have overlapping y coordinates (map to special value None)

        elif special == 'player_is_falling()':
            player_speed_y = self.get_feature('%s.speed_y' % player)
            return sign(player_speed_y)

        elif special == 'opponent_is_falling()':
            opponent_speed_y = self.get_feature('%s.speed_y' % opponent)
            return sign(opponent_speed_y)

        elif special == 'opponent_is_approaching()':
            player_x = self.get_feature('%s.x' % player)
            opponent_x = self.get_feature('%s.x' % opponent)
            opponent_speed_x = self.get_feature('%s.speed_x' % opponent)
            return sign(player_x - opponent_x) == sign(opponent_speed_x)

        elif special == 'opponent_is_attacking()':
            att_type = self.get_feature('%s.attack.attack_type_id' % opponent)
            return att_type is not None and att_type != 0

        elif special == 'closest_threat_x_distance()':
            player_l = self.get_feature('%s.left' % player)
            player_r = self.get_feature('%s.right' % player)
            player_t = self.get_feature('%s.top' % player)
            player_b = self.get_feature('%s.bottom' % player)
            threats_distance = [] # type: List[int]

            att_type = self.get_feature('%s.attack.attack_type_id' % opponent)
            if att_type is not None and att_type != 0:
                att_l = self.get_feature('%s.attack.hit_area.left' % opponent)
                att_r = self.get_feature('%s.attack.hit_area.right' % opponent)
                att_t = self.get_feature('%s.attack.hit_area.top' % opponent)
                att_b = self.get_feature('%s.attack.hit_area.bottom' % opponent)
                if player_t <= att_b <= player_b or player_t <= att_t <= player_b:
                    threats_distance.append(min(abs(player_r - att_l), abs(player_l - att_r)))

            n_proj = self.get_feature('%s.projectiles.count' % opponent)
            for proj_i in range(n_proj):
                proj_l = self.get_feature('%s.projectiles[%d].hit_area.left' % (opponent, proj_i))
                proj_r = self.get_feature('%s.projectiles[%d].hit_area.right' % (opponent, proj_i))
                proj_t = self.get_feature('%s.projectiles[%d].hit_area.top' % (opponent, proj_i))
                proj_b = self.get_feature('%s.projectiles[%d].hit_area.bottom' % (opponent, proj_i))
                if player_t <= proj_b <= player_b or player_t <= proj_t <= player_b:
                    threats_distance.append(min(abs(player_r - proj_l), abs(player_l - proj_r)))

            if threats_distance:
                value = min(threats_distance) + 1
                if value == 0:
                    return 0 # threat is overlapping (map to closest interval, 0)
                else:
                    return discretize_intervals(value, thresholds=[100, 150]) if self.discretize else value
            else:
                return None # there are no threats (map to special value None)

        elif special == 'attack_x_distance()':
            player_l = self.get_feature('%s.left' % player)
            player_r = self.get_feature('%s.right' % player)
            player_t = self.get_feature('%s.top' % player)
            player_b = self.get_feature('%s.bottom' % player)

            att_type = self.get_feature('%s.attack.attack_type_id' % opponent)
            if att_type is not None and att_type != 0:
                att_l = self.get_feature('%s.attack.hit_area.left' % opponent)
                att_r = self.get_feature('%s.attack.hit_area.right' % opponent)
                att_t = self.get_feature('%s.attack.hit_area.top' % opponent)
                att_b = self.get_feature('%s.attack.hit_area.bottom' % opponent)
                if player_t <= att_b <= player_b or player_t <= att_t <= player_b:
                    value = min(abs(player_r - att_l), abs(player_l - att_r)) + 1
                    if value == 0:
                        return 0 # threat is overlapping (map to closest interval, 0)
                    else:
                        return discretize_intervals(value, thresholds=[100, 150]) if self.discretize else value
            else:
                return None # there are no threats (map to special value None)

        elif special == 'closest_projectile_x_distance()':
            player_l = self.get_feature('%s.left' % player)
            player_r = self.get_feature('%s.right' % player)
            player_t = self.get_feature('%s.top' % player)
            player_b = self.get_feature('%s.bottom' % player)
            projectiles_distance = [] # type: List[int]

            n_proj = self.get_feature('%s.projectiles.count' % opponent)
            for proj_i in range(n_proj):
                proj_l = self.get_feature('%s.projectiles[%d].hit_area.left' % (opponent, proj_i))
                proj_r = self.get_feature('%s.projectiles[%d].hit_area.right' % (opponent, proj_i))
                proj_t = self.get_feature('%s.projectiles[%d].hit_area.top' % (opponent, proj_i))
                proj_b = self.get_feature('%s.projectiles[%d].hit_area.bottom' % (opponent, proj_i))
                if player_t <= proj_b <= player_b or player_t <= proj_t <= player_b:
                    projectiles_distance.append(min(abs(player_r - proj_l), abs(player_l - proj_r)))

            if projectiles_distance:
                value = min(projectiles_distance) + 1
                if value == 0:
                    return 0 # threat is overlapping (map to closest interval, 0)
                else:
                    return discretize_intervals(value, thresholds=[100, 150]) if self.discretize else value
            else:
                return None # there are no threats (map to special value None)

        elif special == 'opponent_is_busy()':
            opp_rem_fr = self.get_feature('%s.remaining_frames' % opponent)
            return opp_rem_fr > 0

        else:
            raise ValueError("Unknown special feature: %s" % special)

    def clear_cache(self):
        self.get_feature.cache_clear()

    def set_frame_data(self, frame_data):
        self.clear_cache()
        self.frame_data = frame_data


    def get_features(self, frame_data, player):
        """
        Returns all the features setup in __init__ for a given player.
        :param frame_data: Frame data (can be a recursive dict or a java object)
        :param player: Identifier for the player ('P1' or 'P2')
        :return: All features setup in __init__ for the given player.
        """
        self.set_frame_data(frame_data)

        values = {}
        for feature in self.features:
            if feature.endswith('()'):
                values[feature] = self.get_special(feature, player)
            else:
                if feature.startswith('self'):
                    values[feature] = self.get_feature(feature.replace('self', player, 1))
                elif feature.startswith('opponent'):
                    values[feature] = self.get_feature(feature.replace('opponent', 'P2' if player == 'P1' else 'P1', 1))
                else:
                    values[feature] = self.get_feature(feature)
                if self.discretize and isinstance(values[feature], int):
                    values[feature] = discretize_intervals(values[feature], thresholds=[5, 50, 300])
        return values





    def get_features_info(self, features: List[str]):
        """
        Extracts all the wanted features from the game state. Features list is
        set in the constructor.
        :param features: desired features
        """
        return {f: self.get_feature_info(f) for f in features}


    def get_feature_info(self, feature):

        match = re.match(
            r'^(?:P1|P2|self|opponent).(remaining_frames|action|action_id|state|state_id|hp|energy|x|y|speed_x|speed_y|left|right|top|bottom)$',
            feature
        )
        if match:
            player_feature = match.group(1)
            if player_feature == 'remaining_frames':
                return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': 3615}
            elif player_feature == 'action':
                return {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': ALL_ACTIONS}
            elif player_feature == 'action_id':
                return {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': list(range(len(ALL_ACTIONS)))}
            elif player_feature == 'state':
                return {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': ['STAND', 'CROUCH', 'AIR', 'DOWN']}
            elif player_feature == 'state_id':
                return {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': list(range(4))}
            elif player_feature == 'hp':
                max_hp = max(self.max_hp)
                return {'iterable': False, 'nullable': False, 'type': int, 'min': 0 if max_hp > 0 else -1000, 'max': max_hp if max_hp > 0 else 0}  # FIXME this is a guess
            elif player_feature == 'energy':
                return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': 1000} # FIXME this is a guess
            elif player_feature == 'x':
                return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': self.stage_size['x']}
            elif player_feature == 'y':
                return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': self.stage_size['y'] * 1.5}
            elif player_feature == 'speed_x':
                return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': 500} # FIXME this is a guess
            elif player_feature == 'speed_y':
                return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': 500} # FIXME this is a guess
            elif player_feature == 'left':
                return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': self.stage_size['x']}
            elif player_feature == 'right':
                return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': self.stage_size['x']}
            elif player_feature == 'top':
                return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': self.stage_size['y'] * 1.5}
            elif player_feature == 'bottom':
                return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': self.stage_size['y'] * 1.5}
            else:
                raise ValueError("Unknown feature: %s" % feature)

        match1 = re.match(
            r'^(?:P1|P2|self|opponent).attack.(speed_x|speed_y|hit_damage|guard_damage|start_add_energy|hit_add_energy|'
            r'guard_add_energy|give_energy|give_guard_recov|attack_type|attack_type_id|impact_x|impact_y)$',
            feature
        )
        match2 = re.match(
            r'^(?:P1|P2|self|opponent).projectiles\[[0-9]+\].(speed_x|speed_y|hit_damage|guard_damage|start_add_energy|'
            r'hit_add_energy|guard_add_energy|give_energy|give_guard_recov|attack_type|attack_type_id|'
            r'impact_x|impact_y)$',
            feature
        )
        if match1 or match2:
            attack_feature = match1.group(1) if match1 else match2.group(1)
            if attack_feature == 'speed_x':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': 500} # FIXME this is a guess
            elif attack_feature == 'speed_y':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': 500} # FIXME this is a guess
            elif attack_feature == 'hit_damage':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': 300}
            elif attack_feature == 'guard_damage':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': 100}
            elif attack_feature == 'start_add_energy':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': -300, 'max': 0}
            elif attack_feature == 'hit_add_energy':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': 50}
            elif attack_feature == 'guard_add_energy':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': 30}
            elif attack_feature == 'give_energy':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': 60}
            elif attack_feature == 'give_guard_recov':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': 30}
            elif attack_feature == 'attack_type':
                return {'iterable': False, 'nullable': bool(match1), 'type': 'enum', 'possible_values': list(range(4))}
            elif attack_feature == 'impact_x':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': 30}
            elif attack_feature == 'impact_y':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': -20, 'max': 0}
            else:
                raise ValueError("Unknown feature: %s" % feature)

        match1 = re.match(
            r'^(?:P1|P2|self|opponent).attack.hit_area.(bottom|top|left|right)$',
            feature
        )
        match2 = re.match(
            r'^(?:P1|P2|self|opponent).projectiles\[[0-9]+\].hit_area.(bottom|top|left|right)$',
            feature
        )
        if match1 or match2:
            hit_area_feature = match1.group(1) if match1 else match2.group(1)
            if hit_area_feature == 'left':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': self.stage_size['x']}
            elif hit_area_feature == 'right':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': self.stage_size['x']}
            elif hit_area_feature == 'top':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': self.stage_size['y'] * 1.5}
            elif hit_area_feature == 'bottom':
                return {'iterable': False, 'nullable': bool(match1), 'type': int, 'min': 0, 'max': self.stage_size['y'] * 1.5}

        match = re.match(
            r'^(?:P1|P2|self|opponent).projectiles.count$',
            feature
        )
        if match:
            return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': 10}

        if feature == 'players_x_distance()':
            return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': self.stage_size['x']}
        if feature == 'players_x_diff()':
            return {'iterable': False, 'nullable': False, 'type': int, 'min': 0, 'max': self.stage_size['x']}
        if feature == 'players_y_distance()':
            return {'iterable': False, 'nullable': False, 'type': int, 'min': -self.stage_size['y'] * 1.5, 'max': self.stage_size['y'] * 1.5}
        if feature == 'players_y_diff()':
            return {'iterable': False, 'nullable': False, 'type': int, 'min': -self.stage_size['y'] * 1.5, 'max': self.stage_size['y'] * 1.5}
        if feature == 'player_is_falling()':
            return {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': [-1, 0, +1]}
        if feature == 'opponent_is_falling()':
            return {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': [-1, 0, +1]}
        if feature == 'opponent_is_approaching()':
            return {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': [True, False]}
        if feature == 'opponent_is_attacking()':
            return {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': [True, False]}
        if feature == 'closest_threat_x_distance()':
            return {'iterable': False, 'nullable': True, 'type': int, 'min': 0, 'max': self.stage_size['x']}
        if feature == 'attack_x_distance()':
            return {'iterable': False, 'nullable': True, 'type': int, 'min': 0, 'max': self.stage_size['x']}
        if feature == 'closest_projectile_x_distance()':
            return {'iterable': False, 'nullable': True, 'type': int, 'min': 0, 'max': self.stage_size['x']}
        if feature == 'opponent_is_busy()':
            return {'iterable': False, 'nullable': False, 'type': 'enum', 'possible_values': [True, False]}

        raise ValueError("Unknown feature %s" % feature)




def discretize_intervals(value: Optional[float], thresholds: List[float]):
    """
    Discretizes a value using a list of thresholds. The thresholds should be of increasing value.
    This function returns:
    - None if value is None
    - 0 if value == 0
    - +len(thresholds) if value is greater than all thresholds and value > 0
    - -len(thresholds) if abs(value) is greater than all thresholds and value < 0
    - +i+1 where i is the smallest index such that thresholds[i] > value if value > 0
    - -i-1 where i is the smallest index such that thresholds[i] > abs(value) if value < 0


    :param value: The value to discretize
    :param thresholds: The thresholds to use for discretization
    :return: The bin containing value
    """
    if value is None:
        return None
    elif value == 0:
        return 0

    found_i = len(thresholds)
    for i, threshold in enumerate(thresholds):
        if threshold > value:
            found_i = i + 1
            break
    return found_i * sign(value)


def sign(value):
    if value > 0:
        return +1
    elif value < 0:
        return -1
    else:
        return 0